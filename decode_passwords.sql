-- Decode Base64 passwords in the users table
-- This script converts the password_hash column from Base64 encoding to plain text

-- For MySQL, you can use FROM_BASE64() function (MySQL 5.6.1+)
UPDATE users 
SET password_hash = CAST(FROM_BASE64(password_hash) AS CHAR);
