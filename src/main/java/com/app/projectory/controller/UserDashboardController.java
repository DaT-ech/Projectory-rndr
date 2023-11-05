package com.app.projectory.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.projectory.dao.ConnectionsRepository;
import com.app.projectory.dao.ProjectRepository;
import com.app.projectory.dao.ProjectTaskRepository;
import com.app.projectory.dao.TodoListCollectionRepository;
import com.app.projectory.dao.TodoListRepository;
import com.app.projectory.dao.UsersRepository;
import com.app.projectory.dto.CurrentUserDetailDto;
import com.app.projectory.dto.ProjectStatusCount;
import com.app.projectory.dto.PublicUserConnectionsDetailDto;
import com.app.projectory.dto.PublicUserPersonalDetailDto;
import com.app.projectory.dto.PublicUserProjectDetailDto;
import com.app.projectory.entity.Project;
import com.app.projectory.entity.ProjectTasks;
import com.app.projectory.entity.Todo;
import com.app.projectory.entity.TodoListCollection;
import com.app.projectory.entity.Users;
import com.app.projectory.service.ProjectService;
import com.app.projectory.service.TodoListCollectionService;
import com.app.projectory.service.userAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/user")
public class UserDashboardController {
	@Autowired
	private userAccountService userServ;
	
	@Autowired
	private TodoListCollectionService todoCollServ;
	
	@Autowired
	TodoListRepository todoData;
	@Autowired
	ProjectRepository projDao;
	@Autowired
	ProjectTaskRepository projTaskDao;
	@Autowired
	TodoListCollectionRepository collectionDao;
//	UserAuthService userLgServ = new UserAuthService();
	@Autowired
	UsersRepository userDao;
	@Autowired
	BCryptPasswordEncoder encoder;	
	@Autowired
	ProjectService projServ;
	@Autowired
	TodoListCollectionService collectionServ;
	@Autowired
	ConnectionsRepository connectionsDao;
	
	
	
	//get authenticated user details
	@GetMapping("/username")    
    public @ResponseBody Users currentUserName(Authentication auth) {	
		
		return userServ.getCurrentUserDetail(auth);
		 
    }
	
	@GetMapping("/dashboard")
	public String displayUserDashboard(Model model, Todo todo, Principal principal, Authentication auth) throws JsonProcessingException {
		long userId = userServ.getUserId(auth);
		List<Project> projectsList = projServ.getProjectForCurrentUser(userId);		
		//List<Project> userProjects = projDao.findAll();
		List<ProjectTasks> projectTasks = projTaskDao.findAll();
		
		List<TodoListCollection> collection = collectionServ.getTodoCollectionForCurrentUser(userId);
		
		List<ProjectStatusCount> StatusCountList = projDao.countProjectStatus(userId);
		Map<String, ProjectStatusCount> StatusCntMap = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		for(ProjectStatusCount obj : StatusCountList)
		StatusCntMap.put(obj.getStatusLabel() , obj);
		
		String jsonString = objectMapper.writeValueAsString(StatusCountList);
		model.addAttribute("ProjectStatusCount",jsonString);
		
		model.addAttribute("itemCount", todoData.count());
		model.addAttribute("user", principal.getName());
		//model.addAttribute("projectCount", projDao.count());   //only works on the repo?
		
		model.addAttribute("projectCount", projDao.count());
		model.addAttribute("projectMembersCount", projDao.count());
		model.addAttribute("projectTaskCount", projTaskDao.count());
		model.addAttribute("project", projectsList);
		model.addAttribute("projectTasks", projectTasks);
		model.addAttribute("todoModel", todo);
		model.addAttribute("TodoCollections", collection);
		
			
		
		model.addAttribute("currentUserDetail", userServ.getCurrentUserDetail(auth));
		model.addAttribute("currentPage", "dashboard");
		 return "/user/user-content-container"; 
		
	}
	
	//for test only
	@GetMapping("/api/todo-list/{collectionId}")
	@ResponseBody
	public List<Todo> fetchTodoList(Authentication auth, @PathVariable long collectionId ){
		List<Todo> fetchedTodoList = todoCollServ.getCurrentUserTodoListByCollection(collectionId);
		return fetchedTodoList;
	}
		
	
	@GetMapping("/profile")
	public String displayUserProfile(Model model, Authentication auth) {
		Users currentUser = userServ.getCurrentUserDetail(auth);
		model.addAttribute("currentUserDetail", currentUser);
		model.addAttribute("currentPage", "profile");
		//model.addAttribute("ownAccount", false);
		model.addAttribute("ownAccount", true);
		model.addAttribute("UserDetailByUsername", currentUser.getUsername());
		return "/user/user-content-container";
	}
	
	@GetMapping("{username}")
	public String displayUserProfileWithUsername(Model model, Authentication auth, @PathVariable("username") String username) {
		Users UserByUsernameResult = userServ.getUserDetailByUsername(username);
		
		  if(UserByUsernameResult==null) 
			  model.addAttribute("UserDetailByUsername", "User does not exist");	
		  else {			  
			  String authUsername = userServ.getCurrentUsername(auth);
			  if(authUsername.equals(username)) {
				  return "redirect:profile";  //redirect to current user profile
			  }
			  else {				 
					model.addAttribute("UserDetailByUsername", UserByUsernameResult.getUsername());
			  }
				  
		  }		  		 		
		  
		
		model.addAttribute("currentUserDetail", userServ.getCurrentUserDetail(auth));
		model.addAttribute("currentPage", "profile");
		model.addAttribute("ownAccount", false);
		return "/user/user-content-container";
	}
	
	
	@GetMapping("/getUserConnectionList")
	public @ResponseBody List<PublicUserConnectionsDetailDto> serveConnectionList(Authentication auth){
		String username = userServ.getCurrentUsername(auth);
		//PublicUserPersonalDetailDto publicUser = userDao.getPublicUserDetail(username);
		List<PublicUserConnectionsDetailDto> publicUserConnections = connectionsDao.findApprovedConnections(username);
		//List<PublicUserProjectDetailDto> publicUserProjects = projDao.findPublicUserProjects(username);
		
		return publicUserConnections;
	}
	
	@GetMapping("/detail")
	public @ResponseBody CurrentUserDetailDto getCurrentUserDetail(Authentication auth) {
		CurrentUserDetailDto userDetail = userDao.getCurrentUserDetail(userServ.getUserId(auth));
		return userDetail;
	}
	@GetMapping("/detail/{username}")
	public @ResponseBody PublicUserPersonalDetailDto getUserDetailByUsername(@PathVariable("username") String username, Authentication auth) {
		/*if(username.equals(userServ.getCurrentUsername(auth)))
			return "redirect:/detail";
		else {*/
		PublicUserPersonalDetailDto publicUser = userDao.getPublicUserDetail(username);
		return publicUser;		
	}
	@GetMapping("/projects/{username}")
	public @ResponseBody List<PublicUserProjectDetailDto> getPublicProjectsByUsername(@PathVariable("username") String username, Authentication auth) {
	List<PublicUserProjectDetailDto> publicUserProjects = projDao.findPublicUserProjects(username);
	return publicUserProjects;
	}
	
	@GetMapping("/connections/{username}")
	public @ResponseBody List<PublicUserConnectionsDetailDto> getConnectionByUsername(@PathVariable("username") String username, Authentication auth) {
		
		List<PublicUserConnectionsDetailDto> UserConnections;
	if(username.equals(userServ.getCurrentUsername(auth))) {
		UserConnections = connectionsDao.findAllConnections(username);
	}
	else {
		UserConnections = connectionsDao.findPublicConnections(username);
	}
		
	
	return UserConnections;
	}
	
	
	

}
