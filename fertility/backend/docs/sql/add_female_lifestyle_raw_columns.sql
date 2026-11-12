-- 여성 설문 원시값·표시 라벨 지원 (test_sessions)
ALTER TABLE test_sessions ADD COLUMN IF NOT EXISTS cigarettes_per_day INTEGER;
ALTER TABLE test_sessions ADD COLUMN IF NOT EXISTS binge_days_per_year INTEGER;
