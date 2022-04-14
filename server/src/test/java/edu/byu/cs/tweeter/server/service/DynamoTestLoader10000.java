package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DAOFactoryProvider;
import edu.byu.cs.tweeter.server.service.UserService;

public class DynamoTestLoader10000 {
    public static void main(String args[]){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        clearTables(dynamoDB);
        addUsers(dynamoDB);
        addFollows(dynamoDB);
        addPosts(dynamoDB);
        System.out.println("Do not forget to reduce capacity after running this setup");
    }

    private static void clearTables(DynamoDB dynamoDB){
        deleteTables(dynamoDB);
        createUserTable(dynamoDB);
        createAuthTokenTable(dynamoDB);
        createFollowsTable(dynamoDB);
        createStoryTable(dynamoDB);
        createFeedTable(dynamoDB);
    }

    private static void deleteTables(DynamoDB dynamoDB){
        Table userTable = dynamoDB.getTable("users");
        Table authTokenTable = dynamoDB.getTable("authTokens");
        Table followsTable = dynamoDB.getTable("follows");
        Table storyTable = dynamoDB.getTable("story");
        Table feedTable = dynamoDB.getTable("feed");

        // DELETE TABLES
        try {
            userTable.delete();
            userTable.waitForDelete();
            System.out.println("Deleted users");
        } catch(Exception e){
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }

        try {
            authTokenTable.delete();
            authTokenTable.waitForDelete();
            System.out.println("Deleted authTokens");
        } catch(Exception e){
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }

        try {
            followsTable.delete();
            followsTable.waitForDelete();
            System.out.println("Deleted follows");
        } catch(Exception e){
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }

        try {
            storyTable.delete();
            storyTable.waitForDelete();
            System.out.println("Deleted story");
        } catch(Exception e){
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }

        try {
            feedTable.delete();
            feedTable.waitForDelete();
            System.out.println("Deleted feed");
        } catch(Exception e){
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }

    }

    private static void createFollowsTable(DynamoDB dynamoDB){
        List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("follower_handle").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("followee_handle").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("follower_handle").withKeyType(KeyType.HASH));
        keySchema.add(new KeySchemaElement().withAttributeName("followee_handle").withKeyType(KeyType.RANGE));

        GlobalSecondaryIndex reverseIndex = new GlobalSecondaryIndex()
                .withIndexName("follows_index")
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(20L).withWriteCapacityUnits(6L))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        List<KeySchemaElement> indexKeySchema = new ArrayList<>();
        indexKeySchema.add(new KeySchemaElement().withAttributeName("followee_handle").withKeyType(KeyType.HASH));
        indexKeySchema.add(new KeySchemaElement().withAttributeName("follower_handle").withKeyType(KeyType.RANGE));

        reverseIndex.setKeySchema(indexKeySchema);


        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("follows")
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(20L)
                            .withWriteCapacityUnits(200L))
                    .withKeySchema(keySchema)
                    .withGlobalSecondaryIndexes(reverseIndex)
                    .withAttributeDefinitions(attributeDefinitions);


            Table followsTable = dynamoDB.createTable(request);
            followsTable.waitForActive();
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void createFeedTable(DynamoDB dynamoDB){
        List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("receiver_alias").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("date_time").withAttributeType("S"));


        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("receiver_alias").withKeyType(KeyType.HASH));
        keySchema.add(new KeySchemaElement().withAttributeName("date_time").withKeyType(KeyType.RANGE));

        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("feed")
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

            Table feedTable = dynamoDB.createTable(request);
            feedTable.waitForActive();
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void createStoryTable(DynamoDB dynamoDB){
        List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("sender_alias").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("date_time").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("sender_alias").withKeyType(KeyType.HASH));
        keySchema.add(new KeySchemaElement().withAttributeName("date_time").withKeyType(KeyType.RANGE));

        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("story")
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

            Table storyTable = dynamoDB.createTable(request);
            storyTable.waitForActive();
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void createAuthTokenTable(DynamoDB dynamoDB){
        List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("token").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("token").withKeyType(KeyType.HASH));

        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("authTokens")
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

            Table authTokenTable = dynamoDB.createTable(request);
            authTokenTable.waitForActive();
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void createUserTable(DynamoDB dynamoDB){
        List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("alias").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("alias").withKeyType(KeyType.HASH));

        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("users")
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(20L)
                            .withWriteCapacityUnits(200L));

            Table userTable = dynamoDB.createTable(request);
            userTable.waitForActive();
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void addUsers(DynamoDB dynamoDB){
        // LOAD THE USERS
        Table userTable = dynamoDB.getTable("users");

        UserService userService = new UserService();


        try {
            System.out.println("Adding Jake Taylor to the database...");
            PutItemOutcome outcome = userTable.putItem(
                    new Item().withPrimaryKey("alias", "@jt")
                            .withString("password", userService.hashPassword("password"))
                            .withString("firstName", "Jake")
                            .withString("lastName", "Taylor")
                            .withString("image", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png")
                            .withInt("followersCount", 10000)
                            .withInt("followingCount", 0));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e){
            System.err.println("Unable to add user: Jake Taylor");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Adding 10000 followers to the database...");

            List<User> users = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                User thisUser = new User("Friend", String.valueOf(i), "@friend" + i, "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

                users.add(thisUser);
            }

            DAOFactoryProvider provider = new DAOFactoryProvider();
            provider.getDaoFactory().getUserDAO().addUserBatch(users);
        }catch(Exception e){
            System.err.println("Unable to add user");
            System.err.println(e.getMessage());
        }


    }

    private static void addFollows(DynamoDB dynamoDB){
        // ADD THE FOLLOWING RELATIONSHIPS

        try {
            System.out.println("Adding relationship: @jt follows @friend1...");
            PutItemOutcome outcome = dynamoDB.getTable("follows").putItem(
                    new Item().withPrimaryKey("follower_handle", "@jt", "followee_handle", "@friend1"));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e){
            System.err.println("Unable to add relationship: @jt follows @friend1");
            System.err.println(e.getMessage());
        }

        System.out.println("Adding follows relationships...");

        TableWriteItems items = new TableWriteItems("follows");

        for (int i = 0; i <10000; i++) {
            Item item = new Item().withPrimaryKey("follower_handle", "@friend" + i, "followee_handle", "@jt");
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items, dynamoDB);
                items = new TableWriteItems("follows");
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items, dynamoDB);
        }
    }

    private static void loopBatchWrite(TableWriteItems items, DynamoDB dynamoDB) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote Follows Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more Follows");
        }
    }

    private static void addPosts(DynamoDB dynamoDB){
        // ADD A POST
        Table storyTable = dynamoDB.getTable("story");
        Table feedTable = dynamoDB.getTable("feed");

        String currentDateTime = new Date().toString();

        try {
            System.out.println("Adding a post to @friend1's story...");
            List<String> mentions = new ArrayList<>();
            mentions.add("@friend2");
            List<String> urls = new ArrayList<>();
            urls.add("facebook.com");

            PutItemOutcome outcome = storyTable.putItem(
                    new Item().withPrimaryKey("sender_alias", "@friend1", "date_time", currentDateTime)
                            .withString("post", "hola compadres @friend2 facebook.com")
                            .withList("mentions", mentions)
                            .withList("urls", urls));
        } catch (Exception e){
            System.err.println("Unable to add post to @friend1's story");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Adding @friend1's post to @jt's feed...");

            List<String> mentions = new ArrayList<>();
            mentions.add("@friend2");
            List<String> urls = new ArrayList<>();
            urls.add("facebook.com");

            PutItemOutcome outcome = feedTable.putItem(
                    new Item().withPrimaryKey("receiver_alias", "@jt", "date_time", currentDateTime)
                            .withString("sender_alias", "@friend1")
                            .withString("post", "hola compadres @friend2 facebook.com")
                            .withList("mentions", mentions)
                            .withList("urls", urls));
        } catch (Exception e){
            System.err.println("Unable to add post to @jt's feed");
            System.err.println(e.getMessage());
        }
    }
}
