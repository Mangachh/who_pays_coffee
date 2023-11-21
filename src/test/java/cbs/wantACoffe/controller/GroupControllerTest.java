package cbs.wantACoffe.controller;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import cbs.wantACoffe.CommonData;
import cbs.wantACoffe.dto.MemberGroup;
import cbs.wantACoffe.dto.group.CreateGroup;
import cbs.wantACoffe.dto.group.GroupModel;
import cbs.wantACoffe.dto.user.RegisteredUserToken;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;
import cbs.wantACoffe.exceptions.MemberAlreadyIsInGroup;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberIsNotAdmin;
import cbs.wantACoffe.repository.IMemberRepo;
import io.micrometer.core.ipc.http.HttpSender.Request;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2_test")
@TestMethodOrder(OrderAnnotation.class)

public class GroupControllerTest {
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;


    private static final String IP = "http://localhost";
    private static final String API = "/coffee/api";
    private static String address;
    private static HttpHeaders header;
    private static RegisteredUser user;
    private static long groupId;

    @Test
    @Order(1)
    void addMember() throws URISyntaxException {
        address = IP + ":" + String.valueOf(port) + API;
        URI uri = new URI(address + "/auth/p/register");

        user = CommonData.getTestUserWithSuffix("_GroupControllerTest");
        ResponseEntity<RegisteredUserToken> regToken = this.restTemplate.postForEntity(uri, user,
                RegisteredUserToken.class);

        System.out.println(regToken);
        header = new HttpHeaders();
        header.add("Authorization",
                regToken.getBody().getHead() + " " + regToken.getBody().getToken());
    
    }
    
    @Test
    @Order(2)
    void testCreateGroup() throws URISyntaxException {
        final String groupName = "Firlois";
        CreateGroup group = new CreateGroup("firolis", groupName);
        URI uri = new URI(address + "/groups/add/group");
        RequestEntity<CreateGroup> request = RequestEntity.post(uri)
                .headers(header)
                .accept(MediaType.APPLICATION_JSON)
                .body(group);

        ResponseEntity<GroupModel> response = this.restTemplate.exchange(request, GroupModel.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(groupName, response.getBody().getName());
        groupId = response.getBody().getId();
    }
    
    @Test
    @Order(3)
    void testCreateGroupNoName() throws URISyntaxException {
        final String groupName = "Firlois";
        CreateGroup group = new CreateGroup("firolis", "");
        URI uri = new URI(address + "/groups/add/group");
        RequestEntity<CreateGroup> request = RequestEntity.post(uri)
                .headers(header)
                .accept(MediaType.APPLICATION_JSON)
                .body(group);

        ResponseEntity<GroupHasNoNameException> response = this.restTemplate.exchange(request,
                GroupHasNoNameException.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

    }
    
    @Test
    @Order(4)
    void testCreateGroupNoNickname() throws URISyntaxException {
        final String groupName = "Firlois";
        CreateGroup group = new CreateGroup("", "asdasd");
        URI uri = new URI(address + "/groups/add/group");
        RequestEntity<CreateGroup> request = RequestEntity.post(uri)
                .headers(header)
                .accept(MediaType.APPLICATION_JSON)
                .body(group);

        ResponseEntity<MemberHasNoNicknameException> response = this.restTemplate.exchange(request,
                MemberHasNoNicknameException.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());

    }
    
    @Test
    @Order(5)
    void testCreateGroupNoAuth() throws URISyntaxException {
        final String groupName = "Firlois";
        CreateGroup group = new CreateGroup("Pepo", groupName);
        URI uri = new URI(address + "/groups/add/group");
        RequestEntity<CreateGroup> request =
                RequestEntity.post(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(group);       
        
        
        ResponseEntity<String> response =
                this.restTemplate.exchange(request, String.class);
        
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
                
    }

    @Test
    @Order(6)
    void testAddMemberToGroup() throws URISyntaxException {
        MemberGroup memberGroup = MemberGroup.builder()
                .groupId(groupId)
                .nickname("Fififi")
                .build();

        URI uri = new URI(address + "/groups/add/member");
        RequestEntity<MemberGroup> request = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(header)
                .body(memberGroup);

        ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }
    
    @Test
    @Order(7)
    void testAddMemberToGroupNoNickname() throws URISyntaxException {
        MemberGroup memberGroup = MemberGroup.builder()
                .groupId(groupId)
                .build();

        URI uri = new URI(address + "/groups/add/member");
        RequestEntity<MemberGroup> request = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(header)
                .body(memberGroup);

        ResponseEntity<MemberHasNoNicknameException> response = this.restTemplate.exchange(request,
                MemberHasNoNicknameException.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    
    @Test
    @Order(8)
    void testAddMemberToGroupAlreadyInGroup() throws URISyntaxException {
        MemberGroup memberGroup = MemberGroup.builder()
                .groupId(groupId)
                .nickname("firo")
                .username(user.getUsername())
                .build();

        URI uri = new URI(address + "/groups/add/member");
        RequestEntity<MemberGroup> request = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(header)
                .body(memberGroup);

        ResponseEntity<MemberAlreadyIsInGroup> response = this.restTemplate.exchange(request,
                MemberAlreadyIsInGroup.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    
    @Test
    @Order(9)
    void testAddMemberToGroupNotInGroup() throws URISyntaxException {
        // guardamos nuevo user
        RegisteredUser regUser = CommonData.getTestUserWithSuffix("_notInGroup");
        URI uri = new URI(address + "/auth/p/register");

        ResponseEntity<RegisteredUserToken> regToken = this.restTemplate.postForEntity(uri, regUser,
                RegisteredUserToken.class);

        MemberGroup memberGroup = MemberGroup.builder()
                .groupId(groupId)
                .nickname("firo")
                .username(user.getUsername())
                .build();

        uri = new URI(address + "/groups/add/member");
        HttpHeaders temHeader = new HttpHeaders();
        temHeader.add("Authorization",
                regToken.getBody().getHead() + " " + regToken.getBody().getToken());
        RequestEntity<MemberGroup> request = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(temHeader)
                .body(memberGroup);

        ResponseEntity<MemberAlreadyIsInGroup> response = this.restTemplate.exchange(request,
                MemberAlreadyIsInGroup.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    
    @Test
    @Order(9)
    void testAddMemberToGroupNotAdmin() throws URISyntaxException {
        // guardamos nuevo user
        RegisteredUser secondUser = CommonData.getTestUserWithSuffix("_notAdmin");
        URI uri = new URI(address + "/auth/p/register");

        ResponseEntity<RegisteredUserToken> secondUserToken = this.restTemplate.postForEntity(uri, secondUser,
                RegisteredUserToken.class);

        // lo metemos como nuevo miembro, todo normal hasta aquí
        MemberGroup memberGroup = MemberGroup.builder()
                .groupId(groupId)
                .nickname("firo")
                .username(secondUser.getUsername())
                .build();

        uri = new URI(address + "/groups/add/member");
        RequestEntity<MemberGroup> request = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(header)
                .body(memberGroup);

        ResponseEntity<String> response = this.restTemplate.exchange(request, String.class);
        // comprobamos que vamos bien...
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // ahora creamos un nuevo miembro que lo meterá el secondReg
        MemberGroup toAddBySecond = MemberGroup.builder()
                .groupId(groupId)
                .nickname("Este no se meterá")
                .build();

        // creamos el header de autorización dle segundo user
        HttpHeaders secondHeader = new HttpHeaders();
        secondHeader.add("Authorization",
                secondUserToken.getBody().getHead() + " " + secondUserToken.getBody().getToken());

        request = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .headers(secondHeader)
                .body(toAddBySecond);
        ResponseEntity<MemberIsNotAdmin> responseNot = restTemplate.exchange(request, MemberIsNotAdmin.class);
        assertEquals(HttpStatusCode.valueOf(403), responseNot.getStatusCode());
    }
    
    @Test
    @Order(10)
    void testGetAllMembersFromGroup() throws URISyntaxException {
        URI uri = new URI(API + "/groups/get/members/group/" + String.valueOf(groupId));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", header.get("Authorization").get(0));
        RequestEntity<Void> request = RequestEntity.get(uri).headers(header).build();

        ResponseEntity<MemberGroup[]> response = restTemplate.exchange(request, MemberGroup[].class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // miramos que esté el único grupo que hemos creado
        assertEquals(groupId, response.getBody()[0].getGroupId());

    }
    
    @Test
    @Order(10)
    void testGetAllMembersFromGroupNoGroup() throws URISyntaxException {
        URI uri = new URI(API + "/groups/get/members/group/" + String.valueOf(2555487L));
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", header.get("Authorization").get(0));
        RequestEntity<Void> request = RequestEntity.get(uri).headers(header).build();
        
        ResponseEntity<GroupNotExistsException> response = restTemplate.exchange(request, GroupNotExistsException.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());

    }   
}
