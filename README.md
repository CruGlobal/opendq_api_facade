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
<p>The following is all of the data that OAF does anything with.  Anything extra will be ignored.</p>

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
            "line1": "String",
            "line2": "String",
            "line3": "String",
            "line4": "String",
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
            "client_integration_id": "String",
            "employee_number": "String"
        },
        "client_updated_at": "yyyy-MM-dd HH:mm:ss",
        "source": {
            "system_id": "String",
            "client_integration_id": "String"
        }
    }
}
</pre>

<h3>cURL Tests</h3>
<strong>Note: These endpoints require an OAuth token to be accessed</strong>

<h4>Match</h4>
<pre>
curl -H "Content-Type: application/json" -H "Authorization: Token" -d '{"person":{"id":"74e97ae1-18f3-11e4-8c21-0800200c9a67","gender":"F","client_integration_id":"1-9G6F5","authentication":{"relay_guid":"74e97ae0-18f3-11e4-8c21-0800200c9a67","employee_relay_guid":null,"google_apps_uid":null,"facebook_uid":null,"key_guid":null},"client_updated_at":null,"source":{"system_id":"OAF","client_integration_id":"1-9G6F5"},"first_name":"3Update3","middle_name":null,"last_name":"3PersonUpdate3","title":"Ms.","suffix":null,"preferred_name":null,"email_address":[{"mdm_communication_id":"0","id":"311036e8-18dc-11e4-8c21-0800200c9a67","email":"update.personupdate3@crutest.org"}],"address":[{"mdm_address_id":"0","id":"311036e6-18dc-11e4-8c21-0800200c9a67","city":"Indianapolis","state":"IN","zip_code":"46235","country":"USA","normalized":false,"line1":"2442 Lakepoint Dr","line2":null,"line3":null,"line4":null},{"mdm_address_id":"0","id":"311036e7-18dc-11e4-8c21-0800200c9a67","city":"Greenfield","state":"IN","zip_code":"46140","country":"USA","normalized":false,"line1":"9435 Evergreen Terr","line2":null,"line3":null,"line4":null}],"phone_number":[{"mdm_communication_id":"0","id":"311036e9-18dc-11e4-8c21-0800200c9a67","number":"4563442532","location":"mobile"}],"linked_identities":[{"system_id":null,"client_integration_id":"1-9G6F5","employee_number":null}]}}'  http://localhost:8080/oaf/rest/match
</pre>

<h4>Match or Add</h4>
<pre>
curl -H "Content-Type: application/json" -H "Authorization: Token" -d '{"person":{"id":"74e97ae1-18f3-11e4-8c21-0800200c9a67","gender":"F","client_integration_id":"1-9G6F5","authentication":{"relay_guid":"74e97ae0-18f3-11e4-8c21-0800200c9a67","employee_relay_guid":null,"google_apps_uid":null,"facebook_uid":null,"key_guid":null},"client_updated_at":null,"source":{"system_id":"OAF","client_integration_id":"1-9G6F5"},"first_name":"3Update3","middle_name":null,"last_name":"3PersonUpdate3","title":"Ms.","suffix":null,"preferred_name":null,"email_address":[{"mdm_communication_id":"0","id":"311036e8-18dc-11e4-8c21-0800200c9a67","email":"update.personupdate3@crutest.org"}],"address":[{"mdm_address_id":"0","id":"311036e6-18dc-11e4-8c21-0800200c9a67","city":"Indianapolis","state":"IN","zip_code":"46235","country":"USA","normalized":false,"line1":"2442 Lakepoint Dr","line2":null,"line3":null,"line4":null},{"mdm_address_id":"0","id":"311036e7-18dc-11e4-8c21-0800200c9a67","city":"Greenfield","state":"IN","zip_code":"46140","country":"USA","normalized":false,"line1":"9435 Evergreen Terr","line2":null,"line3":null,"line4":null}],"phone_number":[{"mdm_communication_id":"0","id":"311036e9-18dc-11e4-8c21-0800200c9a67","number":"4563442532","location":"mobile"}],"linked_identities":[{"system_id":null,"client_integration_id":"1-9G6F5","employee_number":null}]}}'  http://localhost:8080/oaf/rest/match-or-add
</pre>

<h4>Update</h4>
<pre>
curl -H "Content-Type: application/json" -H "Authorization: Token" -d '{"person":{"id":"74e97ae1-18f3-11e4-8c21-0800200c9a67","gender":"F","client_integration_id":"1-9G6F5","authentication":{"relay_guid":"74e97ae0-18f3-11e4-8c21-0800200c9a67","employee_relay_guid":null,"google_apps_uid":null,"facebook_uid":null,"key_guid":null},"client_updated_at":null,"source":{"system_id":"OAF","client_integration_id":"1-9G6F5"},"first_name":"3Update3","middle_name":null,"last_name":"3PersonUpdate3","title":"Ms.","suffix":null,"preferred_name":null,"email_address":[{"mdm_communication_id":"0","id":"311036e8-18dc-11e4-8c21-0800200c9a67","email":"update.personupdate3@crutest.org"}],"address":[{"mdm_address_id":"0","id":"311036e6-18dc-11e4-8c21-0800200c9a67","city":"Indianapolis","state":"IN","zip_code":"46235","country":"USA","normalized":false,"line1":"2442 Lakepoint Dr","line2":null,"line3":null,"line4":null},{"mdm_address_id":"0","id":"311036e7-18dc-11e4-8c21-0800200c9a67","city":"Greenfield","state":"IN","zip_code":"46140","country":"USA","normalized":false,"line1":"9435 Evergreen Terr","line2":null,"line3":null,"line4":null}],"phone_number":[{"mdm_communication_id":"0","id":"311036e9-18dc-11e4-8c21-0800200c9a67","number":"4563442532","location":"mobile"}],"linked_identities":[{"system_id":null,"client_integration_id":"1-9G6F5","employee_number":null}]}}'  http://localhost:8080/oaf/rest/update
</pre>

<h4>Add or Update</h4>
<pre>
curl -H "Content-Type: application/json" -H "Authorization: Token" -d '{"person":{"id":"74e97ae1-18f3-11e4-8c21-0800200c9a67","gender":"F","client_integration_id":"1-9G6F5","authentication":{"relay_guid":"74e97ae0-18f3-11e4-8c21-0800200c9a67","employee_relay_guid":null,"google_apps_uid":null,"facebook_uid":null,"key_guid":null},"client_updated_at":null,"source":{"system_id":"OAF","client_integration_id":"1-9G6F5"},"first_name":"3Update3","middle_name":null,"last_name":"3PersonUpdate3","title":"Ms.","suffix":null,"preferred_name":null,"email_address":[{"mdm_communication_id":"0","id":"311036e8-18dc-11e4-8c21-0800200c9a67","email":"update.personupdate3@crutest.org"}],"address":[{"mdm_address_id":"0","id":"311036e6-18dc-11e4-8c21-0800200c9a67","city":"Indianapolis","state":"IN","zip_code":"46235","country":"USA","normalized":false,"line1":"2442 Lakepoint Dr","line2":null,"line3":null,"line4":null},{"mdm_address_id":"0","id":"311036e7-18dc-11e4-8c21-0800200c9a67","city":"Greenfield","state":"IN","zip_code":"46140","country":"USA","normalized":false,"line1":"9435 Evergreen Terr","line2":null,"line3":null,"line4":null}],"phone_number":[{"mdm_communication_id":"0","id":"311036e9-18dc-11e4-8c21-0800200c9a67","number":"4563442532","location":"mobile"}],"linked_identities":[{"system_id":null,"client_integration_id":"1-9G6F5","employee_number":null}]}}'  http://localhost:8080/oaf/rest/add-or-update
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
