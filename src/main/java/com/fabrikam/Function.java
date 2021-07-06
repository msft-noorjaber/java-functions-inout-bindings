package com.fabrikam;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * The function accepts id, title and description, will create or modify a new document based on the provided id.
     * Two ways to invoke it
     * using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/HttpExample 2. curl "{your host}/api/todoitems/{id}?title=<title>&description=<description>"
     */
    @FunctionName("HttpExample")

    public void run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "todoitems/{id}") HttpRequestMessage<Optional<String>> request,
            @CosmosDBOutput(name = "databaseOut", databaseName = "ToDoList", collectionName = "Items", connectionStringSetting = "CosmosDBConnectionString") OutputBinding<ToDoItem> outputItem,
            @CosmosDBInput(name = "databaseIn", databaseName = "ToDoList", collectionName = "Items", id = "{id}", partitionKey = "{id}", connectionStringSetting = "CosmosDBConnectionString") Optional<ToDoItem> item,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Executing todo list operation");

        // Parse query parameter
        final String title = request.getQueryParameters().get("title");
        final String description = request.getQueryParameters().get("description");
        

        /**
         * // You can replace the code below with the following line:
         * ToDoItem toDoItem = new ToDoItem(id, title, description); 
         */

        ToDoItem toDoItem = item.isPresent() ? item.get() : null;
        
        if(toDoItem != null) {
            // update the existing document.
            item.get().description = description;
            item.get().title = title;
        } else {
            // create a new document, this will also update the document of the provided ID.
            toDoItem = new ToDoItem(id, title, description); 
        }
        //Commit to DB
        outputItem.setValue(toDoItem);
    }
}
