package edu.byu.cs.tweeter.server;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.server.service.UserService;

public class DynamoTestLoader {
    public static void main(String args[]){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        clearTables(dynamoDB);
        addUsers(dynamoDB);
        addFollows(dynamoDB);
        addPosts(dynamoDB);
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
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(6L))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        List<KeySchemaElement> indexKeySchema = new ArrayList<>();
        indexKeySchema.add(new KeySchemaElement().withAttributeName("followee_handle").withKeyType(KeyType.HASH));
        indexKeySchema.add(new KeySchemaElement().withAttributeName("follower_handle").withKeyType(KeyType.RANGE));

        reverseIndex.setKeySchema(indexKeySchema);


        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("follows")
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L))
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
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

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
                            .withInt("followersCount", 1)
                            .withInt("followingCount", 2));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e){
            System.err.println("Unable to add user: Jake Taylor");
            System.err.println(e.getMessage());
        }

        try {
            for (int i = 0; i < 50; i++) {
                System.out.println("Adding Friend " + i + " to the database...");
                PutItemOutcome outcome = userTable.putItem(
                        new Item().withPrimaryKey("alias", "@friend" + i)
                                .withString("password", "peekaboo" + i)
                                .withString("firstName", "Friend")
                                .withString("lastName", String.valueOf(i))
                                .withString("image", "https://picsum.photos/id/237/300/300")
                                .withInt("followersCount", 0)
                                .withInt("followingCount", 0));

                System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            }
        } catch (Exception e){
            System.err.println("Unable to add user");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Adding Friend Two to the database...");
            PutItemOutcome outcome = userTable.putItem(
                    new Item().withPrimaryKey("alias", "@friend2")
                            .withString("password", "peekaboo2")
                            .withString("firstName", "Friend")
                            .withString("lastName", "Two")
                            .withString("image", "https://picsum.photos/id/1010/300/300")
                            .withNumber("followersCount", 2)
                            .withNumber("followingCount", 1));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e){
            System.err.println("Unable to add user: Friend Two");
            System.err.println(e.getMessage());
        }
    }

    private static void addAndRemoveFollows(DynamoDB dynamoDB){
        Table followsTable = dynamoDB.getTable("follows");
        try {
            System.out.println("Adding relationship: @test1 follows @test2...");
            PutItemOutcome outcome = followsTable.putItem(
                    new Item().withPrimaryKey("follower_handle", "@test1", "followee_handle", "@test2"));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e){
            System.err.println("Unable to add relationship: @test1 follows @test2");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Removing relationship: @test1 follows @test2...");
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey("follower_handle", "@test1", "followee_handle", "@test2"));
            DeleteItemOutcome deleteItemOutcome = followsTable.deleteItem(deleteItemSpec);
            System.out.println("Successfully deleted.");

        } catch (Exception e){
            System.err.println("Unable to add relationship: @test1 follows @test2");
            System.err.println(e.getMessage());
        }
    }

    private static void addFollows(DynamoDB dynamoDB){
        // ADD THE FOLLOWING RELATIONSHIPS
        Table followsTable = dynamoDB.getTable("follows");

        try {
            System.out.println("Adding relationship: @jt follows @friend1...");
            PutItemOutcome outcome = followsTable.putItem(
                    new Item().withPrimaryKey("follower_handle", "@jt", "followee_handle", "@friend1")
                            .withString("follower_firstName", "Jake")
                            .withString("follower_lastName", "Taylor")
                            .withString("followee_firstName", "Friend")
                            .withString("followee_lastName", "One"));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e){
            System.err.println("Unable to add relationship: @jt follows @friend1");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Adding relationship: @jt follows @friend2...");
            PutItemOutcome outcome = followsTable.putItem(
                    new Item().withPrimaryKey("follower_handle", "@jt", "followee_handle", "@friend2")
                            .withString("follower_firstName", "Jake")
                            .withString("follower_lastName", "Taylor")
                            .withString("followee_firstName", "Friend")
                            .withString("followee_lastName", "Two"));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e){
            System.err.println("Unable to add relationship: @jt follows @friend2");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Adding relationship: @friend1 follows @friend2...");
            PutItemOutcome outcome = followsTable.putItem(
                    new Item().withPrimaryKey("follower_handle", "@friend1", "followee_handle", "@friend2")
                            .withString("follower_firstName", "Friend")
                            .withString("follower_lastName", "One")
                            .withString("followee_firstName", "Friend")
                            .withString("followee_lastName", "Two"));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e){
            System.err.println("Unable to add relationship: @friend1 follows @friend2");
            System.err.println(e.getMessage());
        }

        try {
            System.out.println("Adding relationship: @friend2 follows @jt...");
            PutItemOutcome outcome = followsTable.putItem(
                    new Item().withPrimaryKey("follower_handle", "@friend2", "followee_handle", "@jt")
                            .withString("follower_firstName", "Friend")
                            .withString("follower_lastName", "Two")
                            .withString("followee_firstName", "Jake")
                            .withString("followee_lastName", "Taylor"));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e){
            System.err.println("Unable to add relationship: @friend2 follows @jt");
            System.err.println(e.getMessage());
        }
    }

    private static void addPosts(DynamoDB dynamoDB){
        // ADD A POST
        Table storyTable = dynamoDB.getTable("story");
        Table feedTable = dynamoDB.getTable("feed");

        String currentDateTime = new Date().toString();

        try {
            for (int i = 0; i < 50; i++) {
                System.out.println("Adding a post to @jt's story...");
                List<String> mentions = new ArrayList<>();
                mentions.add("@friend" + i);
                List<String> urls = new ArrayList<>();
                urls.add("amazon.com");

                PutItemOutcome outcome = storyTable.putItem(
                        new Item().withPrimaryKey("sender_alias", "@jt", "date_time", currentDateTime)
                                .withString("post", "howdy gents @friend" + i + " amazon.com")
                                .withList("mentions", mentions)
                                .withList("urls", urls));
            }
        } catch (Exception e){
            System.err.println("Unable to add post to @jt's story");
            System.err.println(e.getMessage());
        }

        try {
            for (int i = 0; i < 50; i++) {
                System.out.println("Adding @friend2's posts to @jt's feed...");

                List<String> mentions = new ArrayList<>();
                mentions.add("@jt");
                List<String> urls = new ArrayList<>();
                urls.add("amazon.com");

                PutItemOutcome outcome = feedTable.putItem(
                        new Item().withPrimaryKey("receiver_alias", "@jt", "date_time", new Date().toString())
                                .withString("sender_alias", "@friend2")
                                .withString("post", i + "howdy gents @jt amazon.com")
                                .withList("mentions", mentions)
                                .withList("urls", urls));
            }
        } catch (Exception e){
            System.err.println("Unable to add post to @jt's feed");
            System.err.println(e.getMessage());
        }

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

//    private static void getUserBack(DynamoDB dynamoDB){
//        Table userTable = dynamoDB.getTable("tweeter_user");
//
//        try{
//            GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_alias", "@chauzer");
//            Item outcome = userTable.getItem(spec);
//
//            if(outcome == null){
//                System.out.println("Couldn't find @chauzer");
//            }
//            else{
//                String firstName = outcome.get("first_name").toString();
//                System.out.println("@chauzer's first name is " + firstName);
//            }
//        } catch (Exception e){
//            System.err.println("Unable to get user @chauzer");
//            System.err.println(e.getMessage());
//        }
//    }
//
//    private static void getFollowsBack(DynamoDB dynamoDB){
//
//    }
}
