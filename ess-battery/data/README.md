# 데이터 안내

MIT–Stanford Battery Dataset을 사용한다. 원본은 수업에서 전달받은 MATLAB 7.3/HDF5 파일이며,
아래 이름을 유지해 이 프로젝트의 `data/raw/`에 배치한다.
MIT와 Stanford는 데이터 연구기관의 출처이며 지원자의 출신 학교를 뜻하지 않는다.

| 배치 | 파일명 | 사용 범위 |
|---|---|---|
| Batch 1 | `2017-05-12_batchdata_updated_struct_errorcorrect.mat` | DAY1 EDA, DAY2 학습·내부 검증 |
| Batch 2 | `2018-02-20_batchdata_updated_struct_errorcorrect.mat` | DAY1 EDA, DAY2 최종 평가 |
| Batch 3 | `2018-04-12_batchdata_updated_struct_errorcorrect.mat` | DAY1 EDA; DAY2 추가 평가는 생략 |

원래 분석에서는 원본 MAT 세 파일을 사용했다. 이 제출본에는 대용량 MAT 파일이 없으며
`.gitignore`로도 제외한다. 전체 분석을 재실행하려면 별도로 준비해야 한다. 노트북은 프로젝트 루트 또는
`notebooks/`를 작업 경로로 사용한다.

## 모델링용 데이터

`processed/features_20261002_125401_694125/`는 현재 성능 결과에 사용한 고정 스냅샷이다.

| 파일 | 내용 |
|---|---|
| `model_candidates.csv` | 모델링 대상 80셀: Batch 1 41셀, Batch 2 39셀; 입력 후보 13개 |
| `label_audit.csv` | 원본 93셀의 수명 보정·제외 사유 |
| `feature_dictionary.csv` | 13개 후보 피처의 정의와 단위 |
| `metadata.json` | 관측 범위, 데이터 구조, 버전, 파일 해시 등 |

입력 관측 범위는 초기 100사이클 이내다. 목표값은 총 수명 `cycle_life_model`이며,
원본 수명과 보정된 모델용 수명을 구분한다. Batch 1의 후속 실험 수명 5개를 반영하고,
미완료 5셀과 Batch 2의 수명 결측 8셀을 회귀 대상에서 제외했다.

02 노트북을 다시 실행하면 새 시각의 스냅샷이 생성된다. 새 스냅샷으로 모델링하려면
03 노트북의 `SNAPSHOT_DIR` 마지막 폴더명을 명시적으로 변경해야 한다.
기존 결과를 확인할 때에는 위 고정 스냅샷을 유지한다.

원본 파일의 기존 출처·해시 기록은
[`data-manifest.json`](data-manifest.json)에 있다.
02 노트북이 이 파일을 사용하므로 함께 보존한다. 제출본에서는 개인 로컬 경로를
상대경로로 바꾸고 수업 안내 페이지 링크를 제외했다. 원본 데이터의 파일명·크기·해시는 유지했다.
원본 로딩은 이 프로젝트의 `data/raw/`에서 수행한다.
