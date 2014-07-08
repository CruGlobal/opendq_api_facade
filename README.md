opendq_api_facade
=================

<h3>Endpoints</h3>

<ul>
  <li>/oaf/rest/match</li>
  <li>/oaf/rest/update</li>
  <li>/oaf/rest/add</li>
  <li>/oaf/rest/match-or-add</li>
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
        "linked_identities": {
            "siebel_contact_id": "String",
            "employee_number": "String"
        },
        "client_updated_at": "yyyy-MM-dd HH:mm:ss"
    }
}
</pre>
