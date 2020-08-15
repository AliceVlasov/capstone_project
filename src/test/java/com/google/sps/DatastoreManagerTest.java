package com.google.sps;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

import org.json.JSONObject;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;

import com.google.sps.DatastoreManager;
import static com.google.sps.DatastoreManager.*;
import static com.google.sps.ChatWebSocket.*;
import com.google.sps.CapstoneAuth;

@PrepareForTest(CapstoneAuth.class)
@RunWith(PowerMockRunner.class)
public class DatastoreManagerTest {

    private static LocalDatastoreHelper datastoreHelper =
            LocalDatastoreHelper.newBuilder()
                                .setConsistency(0.9)
                                .setPort(8081)
                                .setStoreOnDisk(false)
                                .build();

    private static Datastore datastore;
    private static KeyFactory userFactory;
    private static KeyFactory roomFactory;

    private static final String TEST_NAME = "Test User";
    private static final String TEST_UID = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ROOM_NAME = "Test Room";

    @BeforeClass
    public static void setupDatastore() throws IOException,
            InterruptedException {
        datastoreHelper.start();

        datastore = DatastoreOptions.newBuilder()
                                    .setProjectId("chap-2020-capstone")
                                    .setHost("localhost:8081")
                                    .build()
                                    .getService();
        userFactory = datastore.newKeyFactory()
                               .setKind(KIND_USERS);
        roomFactory = datastore.newKeyFactory()
                               .setKind(KIND_CHATROOM);

        getInstance(true);
    }

    @AfterClass
    public static void teardownDatastore() throws IOException,
            InterruptedException, TimeoutException {
        datastoreHelper.stop();
    }

    @Before
    public void setUp() throws IOException {
        datastoreHelper.reset();
    }

    @Test
    public void checkAddMarker() {
        long roomId = 1;
        Entity roomEntity = createRoomEntity(TEST_ROOM_NAME, roomId);
        datastore.put(roomEntity);

        JSONObject markerData = getStandardMarkerNew();
        Key markerKey = createMarkerKey(roomId);

        long returnedId = getInstance().addMarker(markerKey, markerData);

        assertEquals(createMarkerEntity(markerKey, markerData),
            datastore.get(markerKey));
    }

    @Test
    public void checkGetUserName() {
        datastore.put(createUserEntity(TEST_UID, TEST_NAME, TEST_EMAIL));

        assertEquals(TEST_NAME, getInstance().getUserName(TEST_UID));
    }

    @Test
    public void checkCreateChatRoom() {
        datastore.put(createUserEntity(TEST_UID, TEST_NAME, TEST_EMAIL));
        long roomId = getInstance().createChatRoom(TEST_ROOM_NAME, TEST_UID);

        Entity roomEntity = createRoomEntity(TEST_ROOM_NAME, roomId);
        Entity childEntity = createChildRoomEntity(roomId, TEST_UID,
            TEST_ROOM_NAME);

        assertEquals(roomEntity, datastore.get(roomEntity.getKey()));
        assertEquals(childEntity, datastore.get(childEntity.getKey()));
    }

    @Test
    public void checkUserAllowedChatroom() {
        long roomId = 1;

        datastore.put(createRoomEntity(TEST_ROOM_NAME, roomId),
                      createUserEntity(TEST_UID, TEST_NAME, TEST_EMAIL),
                      createChildRoomEntity(roomId, TEST_UID, TEST_ROOM_NAME));

        assertEquals(true,
            getInstance().isUserAllowedChatroom(TEST_UID, roomId));
    }

    @Test
    public void checkAddUserToChatRoom() {
        String addUid = "testuidadd";
        String addName = "Add User";
        String addEmail = "add@example.com";

        long roomId = 1;

        datastore.put(createRoomEntity(TEST_ROOM_NAME, roomId),
                      createUserEntity(TEST_UID, TEST_NAME, TEST_EMAIL),
                      createChildRoomEntity(roomId, TEST_UID, TEST_ROOM_NAME),
                      createUserEntity(addUid, addName, addEmail));

        getInstance().addUserToChatRoom(roomId, addEmail);

        Key childChatRoomKey = datastore.newKeyFactory()
                                        .setKind(KIND_CHATROOM)
                                        .addAncestor(PathElement.of(
                                            KIND_USERS, addUid))
                                        .newKey(roomId);

        assertEquals(true, datastore.get(childChatRoomKey) != null);
    }

    @Test
    public void checkCreateUser() {
        PowerMockito.mockStatic(CapstoneAuth.class);
        when(CapstoneAuth.getUserEmail(TEST_UID)).thenReturn(TEST_EMAIL);

        getInstance().createUser(TEST_UID, TEST_NAME);

        PowerMockito.verifyStatic(CapstoneAuth.class);
        CapstoneAuth.getUserEmail(TEST_UID);

        Entity userEntity = createUserEntity(TEST_UID, TEST_NAME, TEST_EMAIL);

        Entity storedEntity = datastore.get(userEntity.getKey());

        assertEquals(userEntity, storedEntity);
    }

    @Test
    public void checkGetInstance() {
        boolean isNull = (getInstance() == null);

        assertEquals(false, isNull);
    }

    private static JSONObject getStandardMarkerNew() {
        return (new JSONObject()).put(JSON_TITLE, "Title")
                                 .put(JSON_BODY, "Body")
                                 .put(JSON_LATITUDE, 1.5)
                                 .put(JSON_LONGITUDE, -0.3);
    }

    private Key createMarkerKey(long roomId) {
        return datastore.allocateId(datastore.newKeyFactory()
                        .setKind(KIND_CHATROOM_MARKERS)
                        .addAncestor(PathElement.of(
                            KIND_CHATROOM, roomId))
                        .newKey());
    }

    private Entity createMarkerEntity(Key markerKey, JSONObject markerData) {
        Entity markerEntity = Entity.newBuilder(markerKey)
                                    .set(JSON_TITLE,
                                        markerData.getString(JSON_TITLE))
                                    .set(JSON_BODY,
                                        markerData.getString(JSON_BODY))
                                    .set(JSON_LATITUDE,
                                        markerData.getDouble(JSON_LATITUDE))
                                    .set(JSON_LONGITUDE,
                                        markerData.getDouble(JSON_LONGITUDE))
                                    .build();

        return markerEntity;
    }

    private Entity createRoomEntity(String roomName, long roomId) {
        Entity returnEntity = Entity.newBuilder(roomFactory.newKey(roomId))
                                    .set(ROOM_ATTRIBUTE_NAME, roomName)
                                    .build();
        return returnEntity;
    }

    private Entity createChildRoomEntity(long roomId, String parentUid,
            String roomName) {
        Key childRoomKey = datastore.newKeyFactory()
                                    .setKind(KIND_CHATROOM)
                                    .addAncestor(PathElement.of(KIND_USERS,
                                        parentUid))
                                    .newKey(roomId);

        Entity returnEntity = Entity.newBuilder(childRoomKey)
                                    .set(ROOM_ATTRIBUTE_NAME, roomName)
                                    .build();

        return returnEntity;
    }

    private Entity createUserEntity(String uid, String name,
            String email) {
        Entity returnEntity = Entity.newBuilder(userFactory.newKey(uid))
                                    .set(USER_ATTRIBUTE_NAME,
                                        name)
                                    .set(USER_ATTRIBUTE_EMAIL,
                                        email)
                                    .build();
        return returnEntity;
    }
}
