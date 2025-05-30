# MySQL 서버가 준비될 때까지 대기
echo "$(date '+%Y-%m-%d %H:%M:%S') - MySQL 서버 준비 중, 대기 시간: 5초"
sleep 5

mysql -u root -p${MYSQL_ROOT_PASSWORD} <<EOF
CREATE DATABASE IF NOT EXISTS baekgwa_blog;
USE baekgwa_blog;

-- 사용자 생성 및 권한 부여
CREATE USER IF NOT EXISTS '${RDBMS_USERNAME}'@'%' IDENTIFIED BY '${RDBMS_PASSWORD}';
GRANT ALL PRIVILEGES ON baekgwa_blog.* TO '${RDBMS_USERNAME}'@'%';
FLUSH PRIVILEGES;
EOF

# 로그: 쿼리 실행 후
echo "$(date '+%Y-%m-%d %H:%M:%S') - 데이터베이스 및 사용자 생성 완료"

# 스크립트 종료 로그
echo "$(date '+%Y-%m-%d %H:%M:%S') - MySQL 초기화 완료"
