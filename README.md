## Starwallet ##

This app helps you to arrange all your starbucks (Russia) accounts and cards. It parses plas-tek.ru starbucks accounts for the data.

When the SSL certificate is expired:

1. Go to: https://plas-tek.ru/cabinet.aspx?mainlogin=true&style=starbucks with your browser
2. Download the certificate
3. Add it to the java keystore: keytool -import -file /path/to/the/certificate.pem -alias plastek -keystore /Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre/lib/security/cacerts
4. When it prompts for the password type your password for the key store or the default one: changeit