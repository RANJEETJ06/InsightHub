---
marp: true
---


# InsightHub

For educational purpose only,this project allow the user to get some valuable and non valuable insights from a given dataset(in .csv or .xl). 


## 🧰 Tech Stack

**Client:** Not yet implemented (planned: Vue.js)

**Server:** Spring Boot 3 (Java 17)

**Messaging:** RabbitMQ

**Database:** MongoDB(Docker)

**Report Generation:** 
- Apache POI (Excel)
- iText (PDF)
- JFreeChart (for charts: bar, pie, line, scatter, etc.)

**AI Integration:** Gemini API (for advanced data cleaning Not yet)

**Containerization:** Docker, Docker Compose

## 🔧 Microservices Overview

It contains 3 microservices:

| Service                | Description                                               | Technologies Used                     |
|------------------------|-----------------------------------------------------------|----------------------------------------|
| UploadCleanService     | Cleans uploaded CSV using manual rules & Gemini AI        | Spring Boot, Gemini API, RabbitMQ      |
| InsightAnalyzerService | Analyzes cleaned data, encodes categories, finds insights | Spring Boot, MongoDB, RabbitMQ         |
| ReportService          | Generates PDF/Excel reports with charts                   | Spring Boot, JFreeChart, iText, POI    |
## ⚙️ Setup of the Project

### ✅ Prerequisites

Make sure the following are installed and set up:

- [Docker](https://www.docker.com/) and Docker Compose ✅
- Java 17 ✅
- Maven 3.8+ ✅
- Git ✅

---

```bash
git clone https://github.com/RANJEETJ06/InsightHub.git
cd InsightHub


docker-compose up -d

Start RabbitMQ on localhost:5672
(UI: http://localhost:15672, default user/pass: guest/guest)

Start MongoDB on localhost:27017

```


While on InsightHub move to these 3 files and run all the microservices
### Terminal 1
cd uploadClean
./mvnw spring-boot:run

### Terminal 2
cd analysisInsights
./mvnw spring-boot:run

### Terminal 3
cd report
./mvnw spring-boot:run

---

# To Do

✅ After Startup

✅ Upload a CSV via the UploadClean API(`curl` or Postman format)

✅ Processed events will flow automatically via RabbitMQ

✅ Reports (PDF/Excel) will be generated and saved locally

NOTE: After terminating all microservices make sure to remove docker containers by
```bash
docker-compose down

```



