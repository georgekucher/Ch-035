package com.crsms.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crsms.dao.CourseDao;
import com.crsms.domain.Course;
import com.crsms.domain.Module;
import com.crsms.domain.User;

/**
 * 
 * @author Valerii Motresku
 * @author maftey
 *
 */

@Service("courseService")
@Transactional
public class CourseServiceImpl extends BaseServiceImpl<Course> implements CourseService {
	
	@Autowired
    private CourseDao courseDao;
	
	@Autowired
	private AreaService areaService;

	@Autowired
	private ModuleService moduleService;
	
	@Autowired
	private UserService userService;
	
	@Override
	public void save(Course course, long areaId, String ownerEmail) {
		course.setOwner(userService.getUserByEmail(ownerEmail));
		course.setArea(areaService.getAreaById(areaId));
		courseDao.save(course);
	}
	
	@Override
	public List<Course> getAllInitialized() {
		return courseDao.getAllInitialized();
	}
	
	// TODO Ask where to place bellow method. DAO or Service layer	
	@Override
	public Course getInitializedById(Long id, Course.LazyField ... lazyFields) {
		Course course = courseDao.getById(id);
		this.initializeFields(course, lazyFields);
		return course;
	}
	private void initializeFields(Course course, Course.LazyField[] lazyFields) {
		for (Course.LazyField lazyField : lazyFields) {
			this.initializeField(course, lazyField);
		}
	}
	private void initializeField(Course course, Course.LazyField lazyField) {
		switch(lazyField) {
			case ALL:
				Hibernate.initialize(course.getModules());
				for (Module module : course.getModules()) {
					Hibernate.initialize(module.getTests());
					Hibernate.initialize(module.getResources());
				}
				Hibernate.initialize(course.getUsers());
				break;
			case MODULES:
				Hibernate.initialize(course.getModules());
				for (Module module : course.getModules()) {
					Hibernate.initialize(module.getTests());
					Hibernate.initialize(module.getResources());
				}
				break;
			case USERS: 
				Hibernate.initialize(course.getUsers());
		}
	}
	// TODO SEE ABOVE
	
	@Override
	public void update(Course course, long areaId, String ownerEmail) {
		course.setOwner(userService.getUserByEmail(ownerEmail));
		course.setArea(areaService.getAreaById(areaId));
		courseDao.update(course);
	}

	@Override
	public Course get(String name) {
		return courseDao.get(name);
	}

	@Override
	public void delete(Course course) {
		if(course.getPublished()) {
			this.disable(course);
		} else {
			this.disable(course);
			//TODO:replace on HQL
			for(Module module : course.getModules()){
				moduleService.freeResource(module);
			}
			courseDao.delete(course);
		}
	}

	public void disable(Course course) {
		/*course.setDisable(true);
		courseDao.update(course);
		
		for(Module module : course.getModules()){
			moduleService.disable(module);
		}*/
		courseDao.disable(course);
	}

	@Override
	public List<Course> getAllByAreaId(Long areaId) {
		return courseDao.getAllByAreaId(areaId);
	}
	
	@Override
	public void subscribe(Long courseId, String email) {
		Course course = courseDao.getById(courseId);
		User user = userService.getUserByEmail(email);
		course.addUser(user);
		courseDao.update(course);
	}
	
	@Override
	public void unsubscribe(Long courseId, String email) {
		Course course = courseDao.getById(courseId);
		User user = userService.getUserByEmail(email);
		course.deleteUser(user);
		courseDao.update(course);
	}
	
	@Override
	public List<Course> getAllByUserId(Long userId) {
		return courseDao.getAllByUserId(userId);
	}
	
	@Override
	public List<Course> getAllByUserEmail(String email) {
		return courseDao.getAllByUserEmail(email);		
	}
	
	@Override
	public List<Course> getAllByOwnerEmail(String email) {
		return courseDao.getAllByOwnerEmail(email);
	}

	@Override
	public List<Course> searchCourses(String searchWord) {
		return courseDao.searchCourses(searchWord);
	}

}
