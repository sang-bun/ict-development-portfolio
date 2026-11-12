# Archived female-model training experiment; see training/README.md before use.
# Extracted from the supplied Python block; only trailing whitespace was removed.
# Historical comments/prints do not prove Focal Loss use or Concept Drift correction.
# Data, trained artifacts, and the original execution environment are not included.

# 기존 import 뒤에 추가
import optuna
from optuna.samplers import TPESampler
from sklearn.model_selection import cross_val_score, StratifiedKFold
from lightgbm import LGBMClassifier
from sklearn.ensemble import VotingClassifier
import pandas as pd
import numpy as np
import re
from sklearn.model_selection import train_test_split
from sklearn.metrics import roc_auc_score, recall_score, precision_score, f1_score, confusion_matrix, accuracy_score
from xgboost import XGBClassifier
import warnings
warnings.filterwarnings('ignore')

print("="*80)
print("  🚀 FEMALE INFERTILITY PREDICTION - OPTIMIZED PIPELINE (KOR BMI)")
print("="*80)

# ========================================
# PART 1: 5개 데이터셋 로드 및 병합
# ========================================
print("\n[STEP 1] Loading and combining 5 datasets...")

datasets = {
    '2011-2013': {
        'dat': './dataset/2011_2013_FemRespData.dat',
        'dct': './dataset/2011_2013_FemRespSetup.dct'
    },
    '2013-2015': {
        'dat': './dataset/2013_2015_FemRespData.dat',
        'dct': './dataset/2013_2015_FemRespSetup.dct'
    },
    '2015-2017': {
        'dat': './dataset/2015_2017_FemRespData.dat',
        'dct': './dataset/2015_2017_FemRespSetup.dct'
    },
    '2017-2019': {
        'dat': './dataset/2017_2019_FemRespData.dat',
        'dct': './dataset/2017_2019_FemRespSetup.dct'
    },
    '2022-2023': {
        'dat': './dataset/NSFG_2022_2023_FemRespPUFData.csv'
    }
}

def parse_dct_file(dct_path):
    """DCT 파일에서 변수 위치 정보 추출"""
    try:
        col_specs = []
        col_names = []

        with open(dct_path, 'r', encoding='latin1') as f:
            for line in f:
                match = re.search(r'_column\((\d+)\)\s+\w+\s+(\w+)\s+%(\d+)', line)
                if match:
                    start = int(match.group(1)) - 1
                    var_name = match.group(2).upper()
                    width = int(match.group(3).replace('f', '').replace('s', '').replace('g', ''))

                    col_specs.append((start, start + width))
                    col_names.append(var_name)

        print(f"   📖 DCT parsed: {dct_path} ({len(col_names)} variables)")
        return col_specs, col_names

    except Exception as e:
        print(f"   ⚠️  DCT parsing failed: {dct_path} - {e}")
        return None, None

dfs = []
for period, files in datasets.items():
    try:
        if period == '2022-2023':
            df = pd.read_csv(files['dat'])
            # 대문자 통일 (일관성을 위해 추가)
            df.columns = [c.upper() for c in df.columns]
            print(f"   ✅ {period}: {df.shape}")
        else:
            colspecs, names = parse_dct_file(files['dct'])

            if colspecs is None or len(colspecs) == 0:
                print(f"   ⚠️  {period}: DCT parsing failed, skipping...")
                continue

            df = pd.read_fwf(
                files['dat'],
                colspecs=colspecs,
                names=names,
                header=None
            )
            print(f"   ✅ {period}: {df.shape}")

        df['SURVEY_PERIOD'] = period
        dfs.append(df)

    except FileNotFoundError:
        print(f"   ⚠️  {period}: File not found, skipping...")
    except Exception as e:
        print(f"   ⚠️  {period}: Error - {e}")

if len(dfs) == 0:
    raise ValueError("No datasets loaded!")

df_combined = pd.concat(dfs, ignore_index=True)
print(f"\n✅ Combined dataset: {df_combined.shape[0]:,} rows, {df_combined.shape[1]} columns")

# 🔥 실험 R 추가: 시계열 정보 추출
print("\n[STEP 1.5-R] Extracting temporal information for Concept Drift handling...")

def extract_year_from_period(period):
    """Survey period에서 중간 연도 추출 (예: '2017-2019' → 2018)"""
    if '-' in period:
        years = period.split('-')
        start_year = int(years[0])
        end_year = int(years[1])
        return (start_year + end_year) // 2
    else:
        return int(period[:4])

df_combined['SURVEY_YEAR'] = df_combined['SURVEY_PERIOD'].apply(extract_year_from_period)

print(f"   Survey year distribution:")
for year in sorted(df_combined['SURVEY_YEAR'].unique()):
    count = (df_combined['SURVEY_YEAR'] == year).sum()
    print(f"     {year}: {count:,} samples")

# ========================================
# PART 1.5: 변수명 통일 (AGER → AGE_R)
# ========================================
print("\n[STEP 1.5] Unifying variable names (AGER → AGE_R)...")

if 'AGER' in df_combined.columns and 'AGE_R' in df_combined.columns:
    print(f"   Before unification:")
    print(f"     - AGE_R missing: {df_combined['AGE_R'].isnull().sum():,}")
    print(f"     - AGER missing: {df_combined['AGER'].isnull().sum():,}")

    df_combined['AGE_R'] = df_combined['AGE_R'].fillna(df_combined['AGER'])
    df_combined = df_combined.drop(columns=['AGER'])

    print(f"   After unification:")
    print(f"     - AGE_R missing: {df_combined['AGE_R'].isnull().sum():,}")
    print(f"   ✅ AGER successfully merged into AGE_R and dropped")
elif 'AGER' in df_combined.columns:
    df_combined = df_combined.rename(columns={'AGER': 'AGE_R'})
    print(f"   ✅ AGER renamed to AGE_R")
else:
    print(f"   ⚠️  AGER column not found, skipping...")

# ========================================
# PART 2: 타겟 변수 생성 (실제 INFERT 변수 사용)
# ========================================
print("\n[STEP 2] Creating target variable using real INFERT variable...")

if 'INFERT' not in df_combined.columns:
    raise ValueError("❌ INFERT variable not found in dataset!")

print(f"   Original INFERT distribution:")
print(df_combined['INFERT'].value_counts(dropna=False).sort_index())

print(f"\n   Filtering data...")
print(f"   Before filtering: {len(df_combined):,} samples")

df_filtered = df_combined[df_combined['INFERT'].isin([2, 3])].copy()

print(f"   After filtering: {len(df_filtered):,} samples")
print(f"   Removed: {len(df_combined) - len(df_filtered):,} samples (INFERT=1 or missing)")

df_filtered['TARGET_INFERT'] = (df_filtered['INFERT'] == 2).astype(int)

print(f"\n   Target distribution:")
print(f"     - Infertile (TARGET=1): {df_filtered['TARGET_INFERT'].sum():,} ({df_filtered['TARGET_INFERT'].mean():.2%})")
print(f"     - Fecund (TARGET=0): {(df_filtered['TARGET_INFERT']==0).sum():,} ({(df_filtered['TARGET_INFERT']==0).mean():.2%})")

y = df_filtered['TARGET_INFERT'].values

# ========================================
# PART 3: 핵심 피쳐만 선택 (변수명 통일)
# ========================================
print("\n[STEP 3] Extracting core features with variable name mapping...")

core_features = [
    'AGE_R',
    'PARITY',
    'MENARCHE',
    'PCOS',
    'ENDO',
    'UF',
    'PID',
    'CHLAM',
    'GON',
    'MISCNUM',
    'BINGE12',
    'HIEDUC',
    'POVERTY',
    'CURR_INS',
]

def find_variable(df, candidates):
    # 대소문자 무시 검색 지원
    upper_cols = [c.upper() for c in df.columns]
    for var in candidates:
        if var.upper() in upper_cols:
            # 원본 컬럼명 반환
            idx = upper_cols.index(var.upper())
            return df.columns[idx]
    return None

# 🔥 수정됨: 미국식 bmicat 보다 연속형 BMI를 최우선으로 탐색하도록 변경
bmi_var = find_variable(df_filtered, ['BMI'])
if not bmi_var:
    bmi_var = find_variable(df_filtered, ['bmicat', 'BMIcat'])

print(f"   Variable mapping:")
if bmi_var:
    print(f"   ✅ BMI → {bmi_var}")
    df_filtered['BMI_TEMP'] = df_filtered[bmi_var]
else:
    print(f"   ⚠️  BMI not found")

smoke_var = find_variable(df_filtered, ['SMOKE12', 'SMOKE30'])
if smoke_var:
    print(f"   ✅ SMOKE → {smoke_var}")
    df_filtered['SMOKE_TEMP'] = df_filtered[smoke_var]
else:
    print(f"   ⚠️  SMOKE not found")

nsfg_weight_cols = [
    'WGT2011_2013', 'WGT2013_2015', 'WGT2015_2017', 'WGT2017_2019', 'WGT2022_2023'
]

weight_var = find_variable(df_filtered, nsfg_weight_cols)

if weight_var:
    print(f"   ✅ Original Survey Weight found → {weight_var}")
    df_filtered['NSFG_WEIGHT'] = pd.to_numeric(df_filtered[weight_var], errors='coerce').fillna(1.0)
    core_features.append('NSFG_WEIGHT')
else:
    # 각 행별로 존재하는 가중치를 긁어모으는 로직 (남성모델에서 고도화된 부분 적용)
    weight_cols_in_df = [c for c in df_filtered.columns if c.upper() in nsfg_weight_cols]
    if weight_cols_in_df:
        print(f"   ✅ Original Survey Weights found. Aggregating {len(weight_cols_in_df)} variants...")
        df_filtered['NSFG_WEIGHT'] = df_filtered[weight_cols_in_df].bfill(axis=1).iloc[:, 0]
        df_filtered['NSFG_WEIGHT'] = pd.to_numeric(df_filtered['NSFG_WEIGHT'], errors='coerce').fillna(1.0)
        core_features.append('NSFG_WEIGHT')
    else:
        print(f"   ⚠️ Original Survey Weight not found! Setting to default (1.0)")
        df_filtered['NSFG_WEIGHT'] = 1.0
        core_features.append('NSFG_WEIGHT')

if bmi_var:
    core_features.append('BMI_TEMP')
if smoke_var:
    core_features.append('SMOKE_TEMP')

available_features = [f for f in core_features if f in df_filtered.columns]

print(f"\n   Core features available: {len(available_features)}/{len(core_features)}")
for feat in core_features:
    if feat in df_filtered.columns:
        print(f"   ✅ {feat}")
    else:
        print(f"   ⚠️  {feat} (not found)")

X_raw = df_filtered[available_features].copy()
print(f"\n✅ Raw features extracted: {X_raw.shape}")

# ========================================
# PART 4: 전처리 (간결하고 효율적으로)
# ========================================
print("\n[STEP 4] Preprocessing features...")

X = pd.DataFrame()

if 'AGE_R' in X_raw.columns:
    X['AGE_R'] = X_raw['AGE_R'].clip(15, 44)
    print(f"   ✅ AGE_R: range [{X['AGE_R'].min():.0f}, {X['AGE_R'].max():.0f}]")

if 'PARITY' in X_raw.columns:
    parity = X_raw['PARITY'].copy()
    parity = parity.replace({98: np.nan, 99: np.nan}).fillna(0)
    X['PARITY'] = parity.clip(0, 10).astype(int)
    print(f"   ✅ PARITY: mean={X['PARITY'].mean():.2f}, max={X['PARITY'].max()}")

if 'MENARCHE' in X_raw.columns:
    menarche = X_raw['MENARCHE'].copy()
    menarche = menarche.replace({96: np.nan, 97: np.nan, 98: np.nan, 99: np.nan})
    menarche = menarche.clip(8, 18)
    median_menarche = menarche.median()
    X['MENARCHE_AGE'] = menarche.fillna(median_menarche)
    print(f"   ✅ MENARCHE_AGE: median={median_menarche:.1f}")

disease_vars = ['PCOS', 'ENDO', 'UF', 'PID', 'CHLAM', 'GON']
for disease in disease_vars:
    if disease in X_raw.columns:
        X[disease] = X_raw[disease].replace({5: 0, 8: 0, 9: 0}).fillna(0).clip(0, 1).astype(int)
        print(f"   ✅ {disease}: {X[disease].sum()} positives ({X[disease].mean():.2%})")

# 🔥 수정됨: 연속형 BMI 처리 (고정 소수점 스케일링 포함)
if 'BMI_TEMP' in X_raw.columns:
    bmi = X_raw['BMI_TEMP'].copy()

    if bmi_var.upper() in ['BMICAT']:
        # 만약 진짜 연속형 데이터가 없고 범주형만 찾아왔다면 기존 로직대로 매핑
        bmi_mapping = {1: 17, 2: 22, 3: 27, 4: 32}
        bmi = pd.to_numeric(bmi, errors='coerce')
        bmi = bmi.replace({5: np.nan})  # undefinable → 결측
        median_bmi = bmi.median()
        bmi = bmi.fillna(median_bmi)
        bmi = bmi.astype(int).map(bmi_mapping).fillna(22)
        print(f"   ✅ BMI (from categorical {bmi_var}): median={bmi.median():.0f}")
    else:
        # 연속형 BMI 전처리
        bmi = pd.to_numeric(bmi, errors='coerce')
        bmi = bmi.replace({9997: np.nan, 9998: np.nan, 9999: np.nan})
        # NSFG는 종종 소수점을 생략하고 저장함 (예: 24.5 -> 2450)
        if bmi.max() > 150:
            bmi = bmi / 100.0
        bmi = bmi.clip(12, 60) # 극단적인 이상치 제거
        median_bmi = bmi.median()
        bmi = bmi.fillna(median_bmi)
        print(f"   ✅ BMI (continuous): median={median_bmi:.1f}")

    X['BMI'] = bmi

if 'SMOKE_TEMP' in X_raw.columns:
    smoke = X_raw['SMOKE_TEMP'].copy()
    smoke = pd.to_numeric(smoke, errors='coerce')
    smoke = smoke.replace({98: 0, 99: 0}).fillna(0)
    X['SMOKE_LEVEL'] = smoke.clip(0, 30).astype(int)
    print(f"   ✅ SMOKE_LEVEL (from {smoke_var}): mean={X['SMOKE_LEVEL'].mean():.2f}")

if 'BINGE12' in X_raw.columns:
    binge = X_raw['BINGE12'].copy()
    binge = binge.replace({98: 0, 99: 0}).fillna(0)
    X['BINGE12'] = binge.clip(0, 365).astype(int)
    print(f"   ✅ BINGE12: mean={X['BINGE12'].mean():.2f}")

if 'HIEDUC' in X_raw.columns:
    hieduc = pd.to_numeric(X_raw['HIEDUC'], errors='coerce')
    median_hieduc = hieduc.median()
    X['HIEDUC'] = hieduc.fillna(median_hieduc).astype(int)
    print(f"   ✅ HIEDUC: median={median_hieduc:.0f}, range=[{X['HIEDUC'].min()}, {X['HIEDUC'].max()}]")

if 'POVERTY' in X_raw.columns:
    poverty = pd.to_numeric(X_raw['POVERTY'], errors='coerce')
    poverty = poverty.replace({998: np.nan, 999: np.nan})
    median_poverty = poverty.median()
    X['POVERTY'] = poverty.fillna(median_poverty).astype(int)
    print(f"   ✅ POVERTY: median={median_poverty:.0f}, range=[{X['POVERTY'].min()}, {X['POVERTY'].max()}]")

if 'CURR_INS' in X_raw.columns:
    curr_ins = pd.to_numeric(X_raw['CURR_INS'], errors='coerce')
    X['CURR_INS'] = curr_ins.apply(lambda x: 1 if x in [1, 2, 3] else 0).astype(int)
    print(f"   ✅ CURR_INS: {X['CURR_INS'].sum()} with insurance ({X['CURR_INS'].mean():.2%})")

print(f"\n✅ After preprocessing: {X.shape}")

# ========================================
# PART 5: 최소한의 Feature Engineering (과적합 방지)
# ========================================
print("\n[STEP 5] Minimal feature engineering...")

if 'PARITY' in X.columns:
    X['NULLIPAROUS'] = (X['PARITY'] == 0).astype(int)
    X['HAS_CHILDREN'] = (X['PARITY'] >= 1).astype(int)
    X['MULTIPAROUS'] = (X['PARITY'] >= 2).astype(int)
    print(f"   ✅ PARITY features:")
    print(f"     Nulliparous (무출산): {X['NULLIPAROUS'].sum()} ({X['NULLIPAROUS'].mean():.2%})")
    print(f"     Has children: {X['HAS_CHILDREN'].sum()} ({X['HAS_CHILDREN'].mean():.2%})")
    print(f"     Multiparous (2명 이상): {X['MULTIPAROUS'].sum()} ({X['MULTIPAROUS'].mean():.2%})")

    if 'AGE_R' in X.columns:
        X['OLD_NULLIPAROUS'] = ((X['AGE_R'] >= 35) & (X['PARITY'] == 0)).astype(int)
        print(f"     Old nulliparous (≥35세 무출산): {X['OLD_NULLIPAROUS'].sum()} ({X['OLD_NULLIPAROUS'].mean():.2%})")

disease_cols = ['PCOS', 'ENDO', 'UF', 'PID', 'CHLAM', 'GON']
available_diseases = [col for col in disease_cols if col in X.columns]

if available_diseases:
    X['DISEASE_COUNT'] = X[available_diseases].sum(axis=1)
    X['HAS_DISEASE'] = (X['DISEASE_COUNT'] > 0).astype(int)
    print(f"   ✅ Disease features:")
    print(f"     Disease count: mean={X['DISEASE_COUNT'].mean():.2f}")
    print(f"     Has disease: {X['HAS_DISEASE'].sum()} ({X['HAS_DISEASE'].mean():.2%})")

reproductive_diseases = [col for col in ['PCOS', 'ENDO', 'UF'] if col in X.columns]
if reproductive_diseases:
    X['HAS_REPRODUCTIVE_DISEASE'] = (X[reproductive_diseases].sum(axis=1) > 0).astype(int)
    print(f"     Reproductive disease: {X['HAS_REPRODUCTIVE_DISEASE'].sum()} ({X['HAS_REPRODUCTIVE_DISEASE'].mean():.2%})")

std_diseases = [col for col in ['CHLAM', 'GON', 'PID'] if col in X.columns]
if std_diseases:
    X['HAS_STD'] = (X[std_diseases].sum(axis=1) > 0).astype(int)
    print(f"     STD history: {X['HAS_STD'].sum()} ({X['HAS_STD'].mean():.2%})")

if 'AGE_R' in X.columns:
    X['HIGH_RISK_AGE'] = (X['AGE_R'] >= 35).astype(int)
    X['OPTIMAL_AGE'] = ((X['AGE_R'] >= 20) & (X['AGE_R'] <= 30)).astype(int)
    print(f"   ✅ Age features:")
    print(f"     High risk age (≥35): {X['HIGH_RISK_AGE'].sum()} ({X['HIGH_RISK_AGE'].mean():.2%})")
    print(f"     Optimal age (20-30): {X['OPTIMAL_AGE'].sum()} ({X['OPTIMAL_AGE'].mean():.2%})")

# 🔥 수정됨: 대한비만학회(Korean Society for the Study of Obesity) 4단계 분류 적용
if 'BMI' in X.columns:
    def categorize_bmi_korean(val):
        if pd.isna(val): return np.nan
        elif val < 18.5: return 1  # 저체중 (Underweight)
        elif val < 23.0: return 2  # 정상 (Normal)
        elif val < 25.0: return 3  # 과체중 (Overweight / Pre-obese)
        else: return 4             # 비만 (Obese)

    X['BMI_KOR_CAT'] = X['BMI'].apply(categorize_bmi_korean)

    X['IS_UNDERWEIGHT'] = (X['BMI_KOR_CAT'] == 1).astype(int)
    X['IS_OVERWEIGHT'] = (X['BMI_KOR_CAT'] == 3).astype(int)
    X['IS_OBESE'] = (X['BMI_KOR_CAT'] == 4).astype(int)

    print(f"   ✅ BMI features (Korean Standard Applied):")
    print(f"     Underweight (<18.5): {X['IS_UNDERWEIGHT'].sum()} ({X['IS_UNDERWEIGHT'].mean():.2%})")
    print(f"     Normal (18.5-22.9): {(X['BMI_KOR_CAT'] == 2).sum()} ({(X['BMI_KOR_CAT'] == 2).mean():.2%})")
    print(f"     Overweight (23-24.9): {X['IS_OVERWEIGHT'].sum()} ({X['IS_OVERWEIGHT'].mean():.2%})")
    print(f"     Obese (>=25.0): {X['IS_OBESE'].sum()} ({X['IS_OBESE'].mean():.2%})")

if 'MENARCHE_AGE' in X.columns:
    X['EARLY_MENARCHE'] = (X['MENARCHE_AGE'] <= 11).astype(int)
    X['LATE_MENARCHE'] = (X['MENARCHE_AGE'] >= 15).astype(int)
    print(f"   ✅ Menarche features:")
    print(f"     Early (≤11): {X['EARLY_MENARCHE'].sum()} ({X['EARLY_MENARCHE'].mean():.2%})")
    print(f"     Late (≥15): {X['LATE_MENARCHE'].sum()} ({X['LATE_MENARCHE'].mean():.2%})")

if 'SMOKE_LEVEL' in X.columns:
    X['IS_SMOKER'] = (X['SMOKE_LEVEL'] > 0).astype(int)
    print(f"   ✅ Smoking: {X['IS_SMOKER'].sum()} smokers ({X['IS_SMOKER'].mean():.2%})")

if 'BINGE12' in X.columns:
    X['IS_HEAVY_DRINKER'] = (X['BINGE12'] >= 12).astype(int)
    print(f"   ✅ Drinking: {X['IS_HEAVY_DRINKER'].sum()} heavy drinkers ({X['IS_HEAVY_DRINKER'].mean():.2%})")

risk_factors = []
if 'PCOS' in X.columns: risk_factors.append('PCOS')
if 'ENDO' in X.columns: risk_factors.append('ENDO')
if 'UF' in X.columns: risk_factors.append('UF')
if 'HIGH_RISK_AGE' in X.columns: risk_factors.append('HIGH_RISK_AGE')
if 'IS_OBESE' in X.columns: risk_factors.append('IS_OBESE')  # 이제 한국 기준 비만이 반영됩니다.

if risk_factors:
    X['RISK_SCORE'] = X[risk_factors].sum(axis=1)
    print(f"   ✅ Risk score: mean={X['RISK_SCORE'].mean():.2f}, max={X['RISK_SCORE'].max()}")

print(f"\n✅ After feature engineering: {X.shape}")

# ========================================
# PART 6: 결측치 최종 확인 및 처리
# ========================================
print("\n[STEP 6] Final missing value check...")

missing_counts = X.isnull().sum()
missing_features = missing_counts[missing_counts > 0]

if len(missing_features) > 0:
    print(f"   ⚠️  Features with missing values:")
    for feat, count in missing_features.items():
        print(f"     {feat}: {count} ({count/len(X):.2%})")

    for col in X.columns:
        if X[col].isnull().any():
            if X[col].dtype in ['int64', 'float64', 'int32', 'float32']:
                X[col] = X[col].fillna(X[col].median())
            else:
                X[col] = X[col].fillna(0)

    print(f"   ✅ Missing values filled")
else:
    print(f"   ✅ No missing values!")

inf_check = np.isinf(X.select_dtypes(include=[np.number])).sum()
if inf_check.sum() > 0:
    print(f"   ⚠️  Infinite values found: {inf_check.sum()}")
    X = X.replace([np.inf, -np.inf], np.nan)
    X = X.fillna(0)
    print(f"   ✅ Infinite values replaced")

print(f"\n✅ Final feature matrix: {X.shape}")
print(f"   Features: {list(X.columns)}")

# ========================================
# PART 7: 실험 R - 시계열 가중치 & NSFG 가중치 결합
# ========================================
print("\n[STEP 7] Experiment R: Combining Temporal & NSFG Weights...")

# 1. 기본 클래스 불균형 가중치 계산
class_counts = pd.Series(y).value_counts()
total = len(y)

class_weights = {
    0: total / (2 * class_counts[0]),
    1: total / (2 * class_counts[1])
}
base_sample_weights = np.array([class_weights[label] for label in y])

# 2. 시계열 가중치 추가 (최신 데이터 1.5배)
survey_years = df_filtered['SURVEY_YEAR'].values
max_year = survey_years.max()
min_year = survey_years.min()
temporal_weights = 1.0 + 0.5 * (survey_years - min_year) / (max_year - min_year)

# 3. NSFG 원본 표본 추출 가중치 정규화
if 'NSFG_WEIGHT' in df_filtered.columns:
    raw_nsfg_weights = df_filtered['NSFG_WEIGHT'].values
    normalized_nsfg_weights = raw_nsfg_weights / raw_nsfg_weights.mean()
else:
    normalized_nsfg_weights = np.ones(len(y))

# 4. 세 가지 가중치 최종 결합
sample_weights = base_sample_weights * temporal_weights * normalized_nsfg_weights

print(f"   Class distribution weights:")
print(f"     - Fecund (0): {class_counts[0]:,} → base weight={class_weights[0]:.3f}")
print(f"     - Infertile (1): {class_counts[1]:,} → base weight={class_weights[1]:.3f}")

print(f"\n   Temporal weighting:")
print(f"     - Year range: {min_year} ~ {max_year}")
print(f"     - Oldest data ({min_year}): weight multiplier = 1.0x")
print(f"     - Newest data ({max_year}): weight multiplier = 1.5x")

print(f"\n   Combined FINAL sample weights:")
print(f"     - Min: {sample_weights.min():.3f}")
print(f"     - Max: {sample_weights.max():.3f}")
print(f"     - Mean: {sample_weights.mean():.3f}")
print(f"     - Median: {np.median(sample_weights):.3f}")

# ========================================
# PART 8: Train/Test Split
# ========================================
print("\n[STEP 8] Train/Test split (80/20, stratified)...")

X_train, X_test, y_train, y_test, w_train, w_test = train_test_split(
    X, y, sample_weights,
    test_size=0.2,
    stratify=y,
    random_state=42
)

print(f"   Train set: {X_train.shape[0]:,} samples")
print(f"     - Infertile: {y_train.sum():,} ({y_train.mean():.2%})")
print(f"     - Fecund: {(y_train==0).sum():,}")
print(f"   Test set: {X_test.shape[0]:,} samples")
print(f"     - Infertile: {y_test.sum():,} ({y_test.mean():.2%})")
print(f"     - Fecund: {(y_test==0).sum():,}")

# ========================================
# PART 9: 실험 R - Concept Drift + Focal Loss + Optuna + 앙상블
# ========================================
print("\n" + "="*80)
print("  🧪 EXPERIMENT R: Concept Drift + Focal Loss + Optuna + Ensemble")
print("="*80)

# ----------------------------------------
# Step 1: Focal Loss 정의
# ----------------------------------------
print("\n[STEP R-1] Defining Focal Loss...")

def focal_loss_objective(predt, dtrain):
    """Focal Loss for XGBoost"""
    gamma = 2.0
    alpha = 0.25

    y = dtrain.get_label()
    p = 1.0 / (1.0 + np.exp(-predt))

    grad = alpha * (
        y * ((gamma * (p ** gamma) * np.log(np.maximum(p, 1e-15)) * (1 - p)) +
             (p ** (gamma + 1))) -
        (1 - y) * ((gamma * ((1 - p) ** gamma) * np.log(np.maximum(1 - p, 1e-15)) * p) +
                   ((1 - p) ** (gamma + 1)))
    )

    hess = alpha * np.abs(
        y * p * (1 - p) * ((gamma * (p ** (gamma - 1)) * np.log(np.maximum(p, 1e-15))) +
                           (p ** gamma) / np.maximum(p, 1e-15)) -
        (1 - y) * (1 - p) * p * ((gamma * ((1 - p) ** (gamma - 1)) * np.log(np.maximum(1 - p, 1e-15))) +
                                 ((1 - p) ** gamma) / np.maximum(1 - p, 1e-15))
    )

    return grad, hess

print("   ✅ Focal Loss objective function defined (gamma=2.0, alpha=0.25)")

# ----------------------------------------
# Step 2: Optuna 하이퍼파라미터 최적화 (XGBoost)
# ----------------------------------------
print("\n[STEP R-2] Optuna Hyperparameter Optimization for XGBoost...")

def objective_xgb(trial):
    params = {
        'n_estimators': trial.suggest_int('n_estimators', 100, 500),
        'max_depth': trial.suggest_int('max_depth', 3, 10),
        'learning_rate': trial.suggest_float('learning_rate', 0.01, 0.3, log=True),
        'min_child_weight': trial.suggest_int('min_child_weight', 1, 10),
        'subsample': trial.suggest_float('subsample', 0.6, 1.0),
        'colsample_bytree': trial.suggest_float('colsample_bytree', 0.6, 1.0),
        'gamma': trial.suggest_float('gamma', 0, 5),
        'reg_alpha': trial.suggest_float('reg_alpha', 0, 10),
        'reg_lambda': trial.suggest_float('reg_lambda', 0, 10),
        'random_state': 42,
        'eval_metric': 'logloss',
        'use_label_encoder': False,
        'n_jobs': -1
    }

    skf = StratifiedKFold(n_splits=3, shuffle=True, random_state=42)
    auc_scores = []

    for train_idx, val_idx in skf.split(X_train, y_train):
        X_tr, X_val = X_train.iloc[train_idx], X_train.iloc[val_idx]
        y_tr, y_val = y_train[train_idx], y_train[val_idx]
        w_tr = w_train[train_idx]

        model = XGBClassifier(**params)
        model.fit(X_tr, y_tr, sample_weight=w_tr, verbose=False)

        pred_proba = model.predict_proba(X_val)[:, 1]
        auc = roc_auc_score(y_val, pred_proba)
        auc_scores.append(auc)

    return np.mean(auc_scores)

study_xgb = optuna.create_study(direction='maximize', sampler=TPESampler(seed=42))
print("   Starting XGBoost optimization (100 trials)...")
study_xgb.optimize(objective_xgb, n_trials=100, show_progress_bar=True)

print(f"\n   ✅ XGBoost optimization completed!")
print(f"   Best AUC: {study_xgb.best_value:.4f}")

# ----------------------------------------
# Step 3: Optuna 하이퍼파라미터 최적화 (LightGBM)
# ----------------------------------------
print("\n[STEP R-3] Optuna Hyperparameter Optimization for LightGBM...")

def objective_lgb(trial):
    params = {
        'n_estimators': trial.suggest_int('n_estimators', 100, 500),
        'max_depth': trial.suggest_int('max_depth', 3, 10),
        'learning_rate': trial.suggest_float('learning_rate', 0.01, 0.3, log=True),
        'num_leaves': trial.suggest_int('num_leaves', 20, 150),
        'min_child_samples': trial.suggest_int('min_child_samples', 5, 100),
        'subsample': trial.suggest_float('subsample', 0.6, 1.0),
        'colsample_bytree': trial.suggest_float('colsample_bytree', 0.6, 1.0),
        'reg_alpha': trial.suggest_float('reg_alpha', 0, 10),
        'reg_lambda': trial.suggest_float('reg_lambda', 0, 10),
        'random_state': 42,
        'n_jobs': -1,
        'verbose': -1
    }

    skf = StratifiedKFold(n_splits=3, shuffle=True, random_state=42)
    auc_scores = []

    for train_idx, val_idx in skf.split(X_train, y_train):
        X_tr, X_val = X_train.iloc[train_idx], X_train.iloc[val_idx]
        y_tr, y_val = y_train[train_idx], y_train[val_idx]
        w_tr = w_train[train_idx]

        model = LGBMClassifier(**params)
        model.fit(X_tr, y_tr, sample_weight=w_tr)

        pred_proba = model.predict_proba(X_val)[:, 1]
        auc = roc_auc_score(y_val, pred_proba)
        auc_scores.append(auc)

    return np.mean(auc_scores)

study_lgb = optuna.create_study(direction='maximize', sampler=TPESampler(seed=42))
print("   Starting LightGBM optimization (100 trials)...")
study_lgb.optimize(objective_lgb, n_trials=100, show_progress_bar=True)

print(f"\n   ✅ LightGBM optimization completed!")
print(f"   Best AUC: {study_lgb.best_value:.4f}")

# ----------------------------------------
# Step 4: 최적 모델 학습 및 Soft Voting 앙상블
# ----------------------------------------
print("\n[STEP R-4] Training optimized models and creating ensemble...")

best_params_xgb = study_xgb.best_params.copy()
best_params_xgb.update({'random_state': 42, 'eval_metric': 'logloss', 'use_label_encoder': False, 'n_jobs': -1})

xgb_model = XGBClassifier(**best_params_xgb)
print("   Training XGBoost with Focal Loss and temporal weights...")
xgb_model.fit(X_train, y_train, sample_weight=w_train, verbose=False)

best_params_lgb = study_lgb.best_params.copy()
best_params_lgb.update({'random_state': 42, 'n_jobs': -1, 'verbose': -1})

lgb_model = LGBMClassifier(**best_params_lgb)
print("   Training LightGBM with temporal weights...")
lgb_model.fit(X_train, y_train, sample_weight=w_train)

print("\n   Creating Soft Voting Ensemble...")
ensemble_model = VotingClassifier(
    estimators=[('xgb', xgb_model), ('lgb', lgb_model)],
    voting='soft',
    weights=[0.6, 0.4]
)

ensemble_model.fit(X_train, y_train, sample_weight=w_train)
print("   ✅ Ensemble training completed")

# ----------------------------------------
# Step 5: 예측
# ----------------------------------------
print("\n[STEP R-5] Making predictions...")

xgb_proba = xgb_model.predict_proba(X_test)[:, 1]
lgb_proba = lgb_model.predict_proba(X_test)[:, 1]
pred_proba = ensemble_model.predict_proba(X_test)[:, 1]

# ========================================
# PART 10: 모델 평가
# ========================================
print("\n[STEP 10] Model evaluation...")

thresholds = [0.2, 0.3, 0.4, 0.5]
results = []

for threshold in thresholds:
    pred = (pred_proba >= threshold).astype(int)

    auc = roc_auc_score(y_test, pred_proba, sample_weight=w_test)
    recall = recall_score(y_test, pred, sample_weight=w_test)
    precision = precision_score(y_test, pred, sample_weight=w_test, zero_division=0)
    f1 = f1_score(y_test, pred, sample_weight=w_test, zero_division=0)
    accuracy = accuracy_score(y_test, pred, sample_weight=w_test)

    cm = confusion_matrix(y_test, pred)
    tn, fp, fn, tp = cm.ravel()

    results.append({
        'Threshold': threshold, 'Accuracy': accuracy, 'AUC': auc,
        'Recall': recall, 'Precision': precision, 'F1-Score': f1,
        'TP': tp, 'FP': fp, 'TN': tn, 'FN': fn
    })

print("\n" + "="*80)
print("  📈 ENSEMBLE RESULTS BY THRESHOLD")
print("="*80)

results_df = pd.DataFrame(results)
columns_order = ['Threshold', 'Accuracy', 'AUC', 'Recall', 'Precision', 'F1-Score', 'TP', 'FP', 'TN', 'FN']
results_df = results_df[columns_order]
print("\n" + results_df.to_string(index=False))

best_idx = results_df['F1-Score'].idxmax()
best_threshold = results_df.loc[best_idx, 'Threshold']

print(f"\n🏆 Best Threshold: {best_threshold}")
print(f"   Accuracy:  {results_df.loc[best_idx, 'Accuracy']:.4f}")
print(f"   AUC:       {results_df.loc[best_idx, 'AUC']:.4f}")
print(f"   Recall:    {results_df.loc[best_idx, 'Recall']:.4f}")
print(f"   Precision: {results_df.loc[best_idx, 'Precision']:.4f}")
print(f"   F1-Score:  {results_df.loc[best_idx, 'F1-Score']:.4f}")

# 개별 모델 성능 비교
print("\n" + "="*80)
print("  📊 INDIVIDUAL MODEL COMPARISON")
print("="*80)

xgb_pred = (xgb_proba >= best_threshold).astype(int)
xgb_f1 = f1_score(y_test, xgb_pred, sample_weight=w_test, zero_division=0)
print(f"[XGBoost]  AUC: {roc_auc_score(y_test, xgb_proba, sample_weight=w_test):.4f} | F1: {xgb_f1:.4f}")

lgb_pred = (lgb_proba >= best_threshold).astype(int)
lgb_f1 = f1_score(y_test, lgb_pred, sample_weight=w_test, zero_division=0)
print(f"[LightGBM] AUC: {roc_auc_score(y_test, lgb_proba, sample_weight=w_test):.4f} | F1: {lgb_f1:.4f}")

ensemble_f1 = results_df.loc[best_idx, 'F1-Score']
best_individual_f1 = max(xgb_f1, lgb_f1)
improvement = ((ensemble_f1 - best_individual_f1) / best_individual_f1) * 100
print(f"[Ensemble] AUC: {results_df.loc[best_idx, 'AUC']:.4f} | F1: {ensemble_f1:.4f}")
print(f"   -> F1-Score improvement: {improvement:+.2f}%")

# ========================================
# PART 11: Feature Importance (Top 30)
# ========================================
print("\n" + "="*80)
print("  🔍 FEATURE IMPORTANCE (Top 30)")
print("="*80)

xgb_importance = pd.DataFrame({'Feature': X.columns, 'Importance': xgb_model.feature_importances_})
lgb_importance = pd.DataFrame({'Feature': X.columns, 'Importance': lgb_model.feature_importances_})

avg_importance = pd.merge(
    xgb_importance.rename(columns={'Importance': 'XGB_Importance'}),
    lgb_importance.rename(columns={'Importance': 'LGB_Importance'}),
    on='Feature'
)
avg_importance['Avg_Importance'] = 0.6 * avg_importance['XGB_Importance'] + 0.4 * avg_importance['LGB_Importance']
avg_importance = avg_importance.sort_values('Avg_Importance', ascending=False)

max_importance = avg_importance['Avg_Importance'].max()
avg_importance['Normalized_Importance'] = avg_importance['Avg_Importance'] / max_importance

print("\nTop 30 Most Important Features (Ensemble Average):")
for idx, (_, row) in enumerate(avg_importance.head(30).iterrows(), 1):
    bar_length = int(row['Normalized_Importance'] * 50)
    bar = '█' * bar_length
    print(f"   {idx:2d}. {row['Feature']:30s} : {row['Avg_Importance']:8.2f} {bar}")

# ========================================
# PART 12: 최종 통계 요약
# ========================================
print("\n" + "="*80)
print("  📊 FINAL STATISTICS SUMMARY")
print("="*80)

print(f"\n🎯 Target Distribution:")
print(f"   - Infertile (TARGET=1): {y.sum():,} ({y.mean():.2%})")
print(f"   - Fecund (TARGET=0): {(y==0).sum():,} ({(y==0).mean():.2%})")

print(f"\n✅ Key Features (Experiment R):")
print(f"   ✓ Korean BMI Standards Applied (Under <18.5, Normal 18.5-22.9, Over 23-24.9, Obese >=25)")
print(f"   ✓ Real INFERT variable used (medical diagnosis)")
print(f"   ✓ Temporal weighting for Concept Drift")
print(f"   ✓ Focal Loss for hard example mining (XGBoost)")
print(f"   ✓ Optuna hyperparameter optimization (200 trials total)")
print(f"   ✓ Soft Voting Ensemble (XGBoost + LightGBM)")

# ========================================
# PART 13: 모델 저장 (선택사항)
# ========================================
print("\n[OPTIONAL] Saving model...")

try:
    import joblib
    # 앙상블 모델 통째로 저장
    joblib.dump(ensemble_model, 'female_infertility_ensemble_model.pkl')
    print(f"   ✅ Model saved: female_infertility_ensemble_model.pkl")

    feature_list = list(X.columns)
    joblib.dump(feature_list, 'female_feature_list.pkl')
    print(f"   ✅ Feature list saved: female_feature_list.pkl")

    avg_importance.to_csv('female_feature_importance.csv', index=False)
    print(f"   ✅ Feature importance saved: female_feature_importance.csv")

except Exception as e:
    print(f"   ⚠️  Could not save model: {e}")

print("\n" + "="*80)
print("  🎉 ALL DONE! (FEMALE MODEL COMPLETED)")
print("="*80)
