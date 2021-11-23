FROM openjdk:8-jre
COPY bin /app
EXPOSE 9000 9443
CMD /app/bin/beverly-api -Dhttps.port=9443 -Dplay.crypto.secret=supersecret