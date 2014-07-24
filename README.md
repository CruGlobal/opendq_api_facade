opendq_api_facade
=================

<h3>Endpoints</h3>

<ul>
  <li>/oaf/rest/match</li>
  <li>/oaf/rest/update</li>
  <li>/oaf/rest/add</li>
  <li>/oaf/rest/match-or-add</li>
  <li>/oaf/rest/add-or-update</li>
  <li>/oaf/rest/delete</li>
</ul>

<h3>Data</h3>

<pre>
{
    "person": {
        "id": "String",
        "email_address": [  //Could be single object or array
            {
                "id": "String",
                "email": "String"
            },
            {
                "id": "String",
                "email": "String"
            }
        ],
        "last_name": "String",
        "first_name": "String",
        "middle_name": "String",
        "preferred_name": "String",
        "title": "String",
        "suffix": "String",
        "gender": "String",
        "phone_number": {   //Could be single object or array
            "id": "String",
            "number": "String",
            "location": "String"
        },
        "client_integration_id": "String",
        "account_number": "String",
        "address": {    //Could be single object or array
            "id": "String",
            "address_1": "String",
            "address_2": "String",
            "address_3": "String",
            "address_4": "String",
            "city": "String",
            "state": "String",
            "zip_code": "String",
            "country": "String"
        },
        "authentication": {
            "relay_guid": "String"
        },
        "linked_identities": { //Could be single object or array
            "system_id": "String",
            "client_integration_id": "String"
        },
        "client_updated_at": "yyyy-MM-dd HH:mm:ss"
    }
}
</pre>

<h3>cURL Tests</h3>
<h4>Match or Add</h4>
<pre>
curl -H "Content-Type: application/json" -d '{"person":{"id":"k3rfjs3-f8g9-hfi8-5521-12a6er5423","email_address":{"id":"455322","email":"wee@wee.net"},"last_name":"Vom","first_name":"Boo","middle_name":"Slam","preferred_name":"Pogs","title":"Mr.","suffix":"II","gender":"Male","phone_number":{"id":"885462","number":"5555555555","location":"mobile"},"client_integration_id":"123456","address":{"id":"4459874","address_1":"Line 1","address_2":"Line 2","city":"Orlando","state":"FL","zip_code":"32832","country":"USA"},"authentication":{"relay_guid":"f435f4-5f5e-8934-fjda-jk2354oia"},"account_number":"123456789","linked_identities":{"siebel_contact_id":"1-FG32A","employee_number":"012345678"},"client_updated_at":"2014-06-21 13:41:21"}}'  http://localhost:8080/oaf/rest/match-or-add
</pre>

<h4>Update</h4>
<pre>
curl -H "Content-Type: application/json" -d '{"person":{"id":"54sf6-53ef5-f4a53-5af56a","email_address":{"id":"12345","email":"play@bum.com"},"last_name":"Bum","first_name":"Play","title":"Mr.","gender":"Male","phone_number":{"id":"987654","number":"2222222222","location":"mobile"},"client_integration_id":"555555","address":{"id":"456123","address_1":"54 Oaf Dr","address_2":"Apt D","city":"Orlando","state":"FL","zip_code":"32828","country":"USA"},"authentication":{"relay_guid":"534f-a5ef-7f35-fa4s-adf54a8g8"},"account_number":"5463289","linked_identities":{"siebel_contact_id":"1-D46SA","employee_number":"987654321"},"client_updated_at":"2014-06-21 13:41:21"}}'  http://localhost:8080/oaf/rest/update
</pre>

<h4>Add or Update</h4>
<pre>
curl -H "Content-Type: application/json" -d '{"person":{"id":"2ad345e","email_address":{"id":"2a","email":"nh@hn.net"},"last_name":"Nh","first_name":"Hn","title":"Mrs.","gender":"Female","phone_number":{"id":"2b","number":"3333334444","location":"mobile"},"client_integration_id":"2a","address":{"id":"2a","address_1":"55 James St","city":"Orlando","state":"FL","zip_code":"32825","country":"USA"},"authentication":{"relay_guid":"2a"},"account_number":"2a","linked_identities":{"siebel_contact_id":"2a","employee_number":"2a"},"client_updated_at":"2014-06-21 13:41:21"}}'  http://localhost:8080/oaf/rest/add-or-update
</pre>

<h4>Delete</h4>
<pre>
curl -X DELETE http://localhost:8080/oaf/rest/delete/k3rfjs3-f8g9-hfi8-5521-12a6er5423
</pre>
