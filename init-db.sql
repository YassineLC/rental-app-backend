-- Create additional databases for property and booking services
-- user_db is already created by POSTGRES_DB env var

CREATE DATABASE property_db;
CREATE DATABASE booking_db;

GRANT ALL PRIVILEGES ON DATABASE property_db TO rental_user;
GRANT ALL PRIVILEGES ON DATABASE booking_db TO rental_user;
