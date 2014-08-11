opendq_api_facade
=================

<h3>Intended Use</h3>
<p>
  This API sits between Global Registry and OpenDQ as an in-between.  The OpenDQ/Infosolve API requires several calls for each action.  For example, to search on the index, one must first call a configuration endpoint and then a search endpoint.  The OpenDQ/Infosolve API also has very generic names for its parameters and variables such as FIELD1 and arg0.
</p>
<p>For this reason, the opendq api facade (OAF) has been created as a nicer API to call from Global Registry.</p>
<p>
  The plan for now is that Global Registry will get some data about a person then call OAF using the <strong>match</strong> endpoint to try and find a match.  If a match is found, Global Registry will then call the <strong>add-or-update</strong> endpoint, which will result in an update of the existing record.  If no match is found, Global Registry will still call the <strong>add-or-update</strong> endpoint, and in this case it will result in adding this person as a new record.
</p>

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
        "title": "String",
        "suffix": "String",
        "gender": "String",
        "phone_number": {   //Could be single object or array
            "id": "String",
            "number": "String",
            "location": "String"
        },
        "client_integration_id": "String",
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
        "authentication": {  //Each of these ids could be either a string or an array of strings
            "relay_guid": "String",
            "employee_relay_guid": "String",
            "google_apps_uid": "String",
            "facebook_uid": "String",
            "key_guid": "String"
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
<strong>Note: These endpoints require an OAuth token to be accessed</strong>

<h4>Match</h4>
<pre>
curl -H "Content-Type: application/json" -d '{"person":{"email_address":{"id":"455322","email":"wee@wee.net"},"last_name":"Vom","first_name":"Boo","middle_name":"Slam","preferred_name":"Pogs","title":"Mr.","suffix":"II","gender":"Male","phone_number":{"id":"885462","number":"5555555555","location":"mobile"},"client_integration_id":"123456","address":{"id":"4459874","address_1":"Line 1","address_2":"Line 2","city":"Orlando","state":"FL","zip_code":"32832","country":"USA"},"authentication":{"relay_guid":"f435f4-5f5e-8934-fjda-jk2354oia"},"account_number":"123456789","linked_identities":{"siebel_contact_id":"1-FG32A","employee_number":"012345678"},"client_updated_at":"2014-06-21 13:41:21"}}'  http://localhost:8080/oaf/rest/match
</pre>

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
curl -X DELETE http://localhost:8080/oaf/rest/delete/645asf4a643r-w3r54a
</pre>

<h3>Responses</h3>

<h5>Success</h5>
<pre>
[{
  matchId: "k3rfjs3-f8g9-hfi8-5521-12a6er5423",
  confidenceLevel: 1.0,  //if fuzzy match, will be between 0.0 and 1.0, if broad match will be between 0.0 and 30.0
  action: "match"  //or "add" or "update" or "delete"
}]
</pre>

<h5>Match Not Found</h5>
<pre>
[{
  matchId: "Not Found",
  confidenceLevel: 0.0,
  action: "match"
}]
</pre>

<h5>Failed Authentication</h5>
<pre>
401: Unauthorized
"You do not have access to this service"
</pre>

<h5>Error</h5>
<p>These will come back as WebApplicationExceptions with a status code of 5xx or 4xx depending on the error.</p>
