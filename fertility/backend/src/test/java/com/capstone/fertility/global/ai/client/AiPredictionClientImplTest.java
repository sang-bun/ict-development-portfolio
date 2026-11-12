package com.capstone.fertility.global.ai.client;

import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.global.ai.config.AiProperties;
import com.capstone.fertility.global.ai.client.dto.req.AiPredictionReqDTO;
import com.capstone.fertility.global.ai.client.dto.res.AiPredictionResDTO;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class AiPredictionClientImplTest {

    private HttpServer server;
    private AiPredictionClientImpl client;
    private AiProperties properties;
    private volatile String lastPath;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());

        properties = new AiProperties();
        properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
        properties.setConnectTimeoutMs(1000);
        properties.setReadTimeoutMs(1000);
        properties.getPrediction().getPath().setMale("/api/predict/male");
        properties.getPrediction().getPath().setFemale("/api/predict/female");

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(properties.getReadTimeoutMs()));

        client = new AiPredictionClientImpl(
                WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build(),
                properties
        );
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void predictMale_success_mapsResponseAndPath() {
        // 새 스펙: top_factors 가변 길이, mission_candidates는 더 이상 내려오지 않음
        registerJsonHandler("/api/predict/male", 200, """
                        {
                          "status":"success",
                          "result":{
                            "gender":"male",
                            "score":75,
                            "risk_probability":25.0,
                            "bmi":24.1,
                            "top_factors":["흡연","수면 부족","BMI 과다"]
                          }
                        }
                        """);
        server.start();

        AiPredictionResDTO.Response response = client.predictMale(dummyRequest());

        assertNotNull(response);
        assertEquals("success", response.status());
        assertNotNull(response.result());
        assertEquals(75, response.result().resolvedAiScore());
        assertEquals(25.0, response.result().riskProbability());
        assertEquals(24.1, response.result().bmi());
        assertEquals(3, response.result().resolvedTopFactors().size());

        assertEquals("/api/predict/male", lastPath);
    }

    /**
     * top_factors가 빈 배열([])로 오는 경우: 위험 요인 없음 상태.
     * 클라이언트는 예외를 던지지 않고, resolvedTopFactors()는 빈 List를 반환해야 한다.
     */
    @Test
    void predict_topFactorsEmpty_returnsEmptyList() {
        registerJsonHandler("/api/predict/female", 200, """
                        {
                          "status":"success",
                          "result":{
                            "gender":"female",
                            "score":92,
                            "risk_probability":4.2,
                            "bmi":21.0,
                            "top_factors":[]
                          }
                        }
                        """);
        server.start();

        AiPredictionResDTO.Response response = client.predictFemale(dummyRequest());

        assertNotNull(response.result());
        List<String> factors = response.result().resolvedTopFactors();
        assertNotNull(factors, "resolvedTopFactors는 절대 null을 반환하지 않아야 한다");
        assertTrue(factors.isEmpty(), "빈 배열이면 size 0 이어야 한다");
        assertEquals(92, response.result().resolvedAiScore());
    }

    /**
     * top_factors가 2개(Top 3 미만)로 오는 경우: 그대로 2개 보존.
     */
    @Test
    void predict_topFactorsTwo_preservesAll() {
        registerJsonHandler("/api/predict/male", 200, """
                        {
                          "status":"success",
                          "result":{
                            "gender":"male",
                            "score":68,
                            "risk_probability":31.0,
                            "bmi":26.5,
                            "top_factors":["음주","스트레스 높음"]
                          }
                        }
                        """);
        server.start();

        AiPredictionResDTO.Response response = client.predictMale(dummyRequest());

        List<String> factors = response.result().resolvedTopFactors();
        assertEquals(2, factors.size());
        assertEquals("음주", factors.get(0));
        assertEquals("스트레스 높음", factors.get(1));
    }

    /**
     * top_factors가 7개 이상(Top 3 초과)으로 오는 경우: 길이 제한 없이 전부 보존.
     */
    @Test
    void predict_topFactorsManyEntries_preservesAll() {
        registerJsonHandler("/api/predict/female", 200, """
                        {
                          "status":"success",
                          "result":{
                            "gender":"female",
                            "score":42,
                            "risk_probability":71.4,
                            "bmi":29.8,
                            "top_factors":["흡연","음주","수면 부족","BMI 과다","스트레스 높음","PCOS","자궁근종"]
                          }
                        }
                        """);
        server.start();

        AiPredictionResDTO.Response response = client.predictFemale(dummyRequest());

        List<String> factors = response.result().resolvedTopFactors();
        assertEquals(7, factors.size());
        assertEquals("흡연", factors.get(0));
        assertEquals("자궁근종", factors.get(6));
    }

    /**
     * 레거시(=제거된) 필드가 응답에 그대로 섞여 들어와도 파싱 실패 없이 무시되어야 한다.
     * - top1_factor / top2_factor / top3_factor / mission_candidates: 모두 ignoreUnknown 처리
     * - top_factors는 정상 매핑되어야 한다.
     */
    @Test
    void predict_legacyFieldsPresent_ignoredGracefully() {
        registerJsonHandler("/api/predict/male", 200, """
                        {
                          "status":"success",
                          "result":{
                            "gender":"male",
                            "score":80,
                            "risk_probability":18.0,
                            "bmi":23.0,
                            "top_factors":["흡연"],
                            "top1_factor":"LEGACY_top1",
                            "top2_factor":"LEGACY_top2",
                            "top3_factor":"LEGACY_top3",
                            "mission_candidates":["LEGACY_m1","LEGACY_m2"]
                          }
                        }
                        """);
        server.start();

        AiPredictionResDTO.Response response = client.predictMale(dummyRequest());

        assertEquals(80, response.result().resolvedAiScore());
        // 레거시 필드는 ignoreUnknown으로 무시되어 정상 파싱되며,
        // top_factors만 1개로 정상 매핑된다.
        assertEquals(1, response.result().resolvedTopFactors().size());
        assertEquals("흡연", response.result().resolvedTopFactors().get(0));
    }

    /**
     * top_factors 키가 아예 누락된 경우에도 resolvedTopFactors()는 빈 List를 반환한다.
     */
    @Test
    void predict_topFactorsMissing_returnsEmptyList() {
        registerJsonHandler("/api/predict/female", 200, """
                        {
                          "status":"success",
                          "result":{
                            "gender":"female",
                            "score":85,
                            "risk_probability":12.0,
                            "bmi":22.0
                          }
                        }
                        """);
        server.start();

        AiPredictionResDTO.Response response = client.predictFemale(dummyRequest());

        List<String> factors = response.result().resolvedTopFactors();
        assertNotNull(factors);
        assertTrue(factors.isEmpty());
    }

    @Test
    void predictFemale_server5xx_throwsGeneralException() {
        registerJsonHandler("/api/predict/female", 500, "boom");
        server.start();

        assertThrows(GeneralException.class, () -> client.predictFemale(dummyRequest()));
    }

    @Test
    void predict_invalidStatus_throwsGeneralException() {
        registerJsonHandler("/api/predict/female", 200, """
                        {
                          "status":"fail",
                          "result":{"score":75}
                        }
                        """);
        server.start();

        assertThrows(GeneralException.class, () -> client.predict(dummyRequest(), Gender.F));
    }

    @Test
    void predict_readTimeout_throwsGeneralException() {
        server.createContext("/api/predict/male", exchange -> {
            lastPath = exchange.getRequestURI().getPath();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            byte[] body = """
                        {
                          "status":"success",
                          "result":{"score":80}
                        }
                        """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();

        assertThrows(GeneralException.class, () -> client.predict(dummyRequest(), Gender.M));
    }

    private void registerJsonHandler(String path, int statusCode, String responseJson) {
        server.createContext(path, exchange -> writeJson(exchange, path, statusCode, responseJson));
    }

    private void writeJson(HttpExchange exchange, String path, int statusCode, String responseJson) throws IOException {
        lastPath = exchange.getRequestURI().getPath();
        if (!path.equals(lastPath)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        byte[] body = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    private static AiPredictionReqDTO.Request dummyRequest() {
        return AiPredictionReqDTO.Request.builder()
                .age(30)
                .height(165.0)
                .weight(55.0)
                .chlam(0)
                .gon(0)
                .smoke30(0)
                .drink12(0)
                .binge12Score(0)
                .stressScore(5)
                .stressLevel("LOW")
                .build();
    }
}
