build:
	./gradlew clean build
start:
	./gradlew run
install:
	./gradlew installDist
start-dist:
	sh ./build/install/app/bin/app
report:
	./gradlew jacocoTestReport
.PHONY: build