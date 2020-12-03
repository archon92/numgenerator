# numgenerator
a simple number generator

## Usage Instructions:
Since this is a simple springboot app we can 
 
 - Clone from source and then run ``mvn clean install`` to generate a jar file in ``target`` which can then be run as
  ``java -jar numgenerator-0.0.1-SNAPSHOT.jar`` or `` mvn springboot:run`` from root
 - The app also has a docker image in docker hub at my personal repo. 
 You can use
 ``docker pull adithyar92/numgen:latest`` and run as 
 
 ``docker run -p 127.0.0.1:80:8080/tcp adithyar92/numgen:latest`` 
  

## Available APIS
### Api to Generate numbers ("/api/generate")

```
curl --location --request POST 'http://localhost:8080/api/generate' \
   --header 'Content-Type: application/json' \
   --data-raw '{
       "goal": "4",
       "step": "2"
   }'
```
Response:
``{
      "task": "0b531e6f-4d2f-44e6-8a80-5f80e97eb3c2"
  }``
  
### Api to get status of a task ("/api/tasks/{UDID}/status")

```
curl --location --request GET 'http://localhost:8080/api/tasks/0b531e6f-4d2f-44e6-8a80-5f80e97eb3c2/status'
```
Response:
``{
      "result": "SUCCESS"
  }``  
 
### Api to get numbers of a task ("/api/tasks/${UDID}?action=num_list")
 
 ```
curl --location --request GET 'http://localhost:8080/api/tasks/0b531e6f-4d2f-44e6-8a80-5f80e97eb3c2?action=num_list'
 ```
Response:
 ``{
       "result": "10,8,6,4,2,0"
   }``
   
### Api for Bulk Generation ("/api/bulkGenerate")
  
  ```
curl --location --request POST 'http://localhost:8080/api/bulkGenerate' \
--header 'Content-Type: application/json' \
--data-raw '[{
    "goal": "4",
    "step": "2"
},
{
    "goal": "6",
    "step": "2"
}
]'
  ```
Response:
``{
      "task": "0b531e6f-4d2f-44e6-8a80-5f80e97eb3c2"
  }``

### Api to get numbers of a Bulk task ("/api/tasks/${UDID}?action=num_list")
 
 ```
curl --location --request GET 'http://localhost:8080/api/tasks/0b531e6f-4d2f-44e6-8a80-5f80e97eb3c2?action=num_list'
 ```
Response:
Assyming the bulk task was for [10,2] and [6,2]

 ``{
       "result": "[10,8,6,4,2,0, ,6,4,2,0]"
   }``