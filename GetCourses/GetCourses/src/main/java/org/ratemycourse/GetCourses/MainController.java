package org.ratemycourse.GetCourses;

import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/api")
public class MainController {

	@Autowired // This means to get the bean called userRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	
	
	
	
	
	private CourseRepository courseRepository;
	
	@Autowired
	private UniqueCourseRepository uniqueCourseRepository;

	@RequestMapping(path="/courses/add", method=RequestMethod.POST)
	public @ResponseBody String addNewCourse (@RequestParam(value="val") long val, 
			@RequestParam(value="name") String name, @RequestParam(value="school") String school, 
			@RequestParam(value="dept") String dept, @RequestParam(value="number") String number,
			@RequestParam(value="description") String description, @RequestParam(value="attrs") String attrs,
			@RequestParam(value="isMain") boolean isMain) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		Course c = new Course();
		String id = dept + "-" + number;
		c.setCommonVal(val);
		c.setId(id);
		c.setSchool(school);
		c.setDept(dept);
		c.setNum(number);
		String[] ats = attrs.split(",");
		c.setAttrs(new HashSet<String>(Arrays.asList(ats)));
		c.setName(name);
		c.setDescription(description);
		c.setMain(isMain);
		if (uniqueCourseRepository.existsById(val)) {
			UniqueCourse uc = uniqueCourseRepository.findById(val).get();
			uc.addSame(c);
			courseRepository.save(c);
		}
		else {
			UniqueCourse uc = new UniqueCourse(val, Arrays.asList(c));
			System.out.println("parent id: " + c.getParent().getId());
			System.out.println("child key: " + ((Course) uc.getSames().toArray()[0]).getId());
			courseRepository.save(c);
			uniqueCourseRepository.save(uc);
			c.setParent(uc);
			courseRepository.save(c);
		}
		return "Saved";
	}
	
	@RequestMapping(path="/findDept", method=RequestMethod.GET)
	public @ResponseBody List<Course> getDepartment(@RequestParam(value="dept") String deptId) {
		return courseRepository.findByDept(deptId);
	}
	
	@RequestMapping(path="/findSchool", method=RequestMethod.GET)
	public @ResponseBody List<Course> getSchool(@RequestParam(value="school") String schoolId) {
		return courseRepository.findBySchool(schoolId);
	}
	
	@RequestMapping(path="/findSchoolAndDept", method = RequestMethod.GET)
	public @ResponseBody List<Course> getSchoolAndDepartment(@RequestParam(value="school") String schoolId, 
			@RequestParam(value="dept") String deptId) {
		return courseRepository.findBySchoolAndDept(schoolId, deptId);
	}

	@GetMapping(path="/courses/all")
	public @ResponseBody Iterable<Course> getAllCourses() {
		// returns JSON with the courses
		return courseRepository.findAll();
	}
	
	@GetMapping(path="/courses/unique")
	public @ResponseBody Iterable<UniqueCourse> getAllUniqueCourses() {
		// returns JSON with the courses
		return uniqueCourseRepository.findAll();
	}
	
	@RequestMapping(value="/courses/{courseKey}", method = RequestMethod.GET)
	public @ResponseBody Optional<Course> getCourse(@PathVariable String courseKey) {
		return courseRepository.findById(courseKey);
	}

}