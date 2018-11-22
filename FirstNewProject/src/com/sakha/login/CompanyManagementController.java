package com.sakha.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sakha.form.CompanyComplianceRelationForm;
import com.sakha.form.CompanyForm;
import com.sakha.form.ComplianceItemsForm;
import com.sakha.form.ContactsForm;
import com.sakha.form.DocumentsForm;
import com.sakha.security.LoginForm;
import com.sakha.service.CompanyComplianceRelationService;
import com.sakha.service.CompanyManagementService;
import com.sakha.service.DocumentService;
import com.sakha.service.LoginService;
import com.sakha.service.RulesServices;
import com.sakha.utility.AppConstant;
import com.sakha.utility.CompanyManagementUriConstant;
import com.sakha.utility.ConfigReader;
import com.sakha.utility.ContactJsonForm;
import com.sakha.utility.DateFormatter;
import com.sakha.utility.RegisterMail;
import com.sakha.utility.UpdateContactJson;

@Controller
public class CompanyManagementController {

	private static final Logger log = Logger.getLogger(CompanyManagementController.class);
	@Autowired
	private CompanyManagementService companymanagementservice;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ConfigReader configReader;
	@Autowired
	private DateFormatter dateFormatter;
	@Autowired
	private RegisterMail registerMail;
	@Autowired
	private CompanyComplianceRelationService companyCompliance;
	@Autowired
	private RulesServices rulesService;
	@Autowired
	private DocumentService documentService;
	
	@RequestMapping(value = CompanyManagementUriConstant.ADD_COMPANY, method = RequestMethod.POST, headers = {"Content-type=application/json"} )
	public ModelAndView registerCompany(ModelMap map, HttpServletRequest req,
			HttpServletResponse res, HttpSession session, @RequestBody ContactJsonForm[] jsonList) throws Exception {
		
		LoginForm loginForm = loginService.getUser(session.getAttribute("userId").toString());
		int response = 0, user_ref_no = loginForm.getId();
		List<CompanyForm> companyDetails = companymanagementservice
				.getCompanyList(user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
		List<String> dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}
		
		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		
		for(ContactJsonForm contactForm: jsonList){
		
				if(contactForm.getCompanyName().length() == 0 || contactForm.getCompanyName().equals("")){
					map.addAttribute("error",
							configReader.getMessage("COMPANY_NAME_EMPTY"));
					return new ModelAndView("CompanyListView");
				} 
				
				if(contactForm.getFirstName().length() == 0 || contactForm.getFirstName().equals("")){
						map.addAttribute("error",
								configReader.getMessage("CONTACT_FIRST_NAME_EMPTY"));
						return new ModelAndView("CompanyListView");
				}
				
				
				if(contactForm.getLastName().length() == 0 || contactForm.getLastName().equals("")){
						map.addAttribute("error",
								configReader.getMessage("CONTACT_LAST_NAME_EMPTY"));
						return new ModelAndView("CompanyListView");
				}
				
				if(contactForm.getEmailId().length() == 0 || contactForm.getEmailId().equals("")){
					map.addAttribute("error",
							configReader.getMessage("CONTACT_EMAIL_EMPTY"));
					return new ModelAndView("CompanyListView");
				}	
				
				if(contactForm.getDesignation().length() == 0 || contactForm.getDesignation().equals("")){
					map.addAttribute("error",
							configReader.getMessage("CONTACT_DESIGNATION_EMPTY"));
					return new ModelAndView("CompanyListView");
				}	
		}
		
		Date creatcompanydate = new Date();
		Timestamp companydate = new Timestamp(creatcompanydate.getTime());

		
		if(jsonList !=null && jsonList.length > 0){
			
			String companyName = jsonList[0].getCompanyName();
			if(companyName != null){
				
				int oldCoRefNo = companymanagementservice.isComapanyExists(companyName, user_ref_no);
		
				if ( oldCoRefNo == 0) { // new company here.
		
					CompanyForm companyform = new CompanyForm();
		
					companyform.setOwner_user_ref_no(user_ref_no);
					companyform.setCo_name(companyName.replace("\"", "'"));
					companyform.setDeleted(false);
					companyform.setCreate_ts(companydate);
					companyform.setLast_update_ts(companydate);
					companyform.setCreate_user_ref_no(user_ref_no);
					companyform.setLast_update_user_ref_no(user_ref_no);
					response = companymanagementservice.addCompany(companyform);
		
					if (response == 0) {
						map.addAttribute("error", configReader.getMessage("COMPANY_CREATION_ERROR"));
					}else{ //add contact for company.

						for(ContactJsonForm contactForm : jsonList){ //getting each contact for a company.
						
							if(!companymanagementservice.isEmployeeExists(response, contactForm.getEmailId())){
									//if another record with the same email id is not available
								
								ContactsForm contactform = new ContactsForm();
								contactform.setContact_type_cd(contactForm.getDesignation().charAt(0));
								contactform.setCo_ref_no(response);
								contactform.setContact_first_name(contactForm.getFirstName().replace("\"", "'"));
								contactform.setContact_last_name(contactForm.getLastName().replace("\"", "'"));
								contactform.setContact_mail_id(contactForm.getEmailId().replace("\"", "'"));
								contactform.setCreate_ts(new Timestamp(new Date().getTime()));
								contactform.setLast_update_ts(new Timestamp(new Date().getTime()));
								contactform.setCreate_user_ref_no(user_ref_no);
								contactform.setLast_update_user_ref_no(user_ref_no);
								contactform.setOwner_user_ref_no(user_ref_no);
								int contactRefNo = companymanagementservice.addCompanyContacts(contactform);
								
								//create a compliance_one user for read only.
								
								//Disabled since 30 Dec 2015 as per discussion
								
/*								if(contactRefNo > 0 && contactForm.getDesignation().charAt(0) == '1'){
									
									String newEmailId = contactForm.getEmailId();
									LoginForm newUser = loginService.getUser(newEmailId);
									if(newUser == null){
										LoginForm ceologinForm = new LoginForm();
										ceologinForm.setUserid(contactForm.getEmailId().replace("\"", "'"));
										ceologinForm.setPassword("");
										ceologinForm.setStatus(false);
										ceologinForm.setUsername(contactForm.getFirstName().replace("\"", "'"));
										ceologinForm.setGarde(1);
										ceologinForm.setDepartment(1);
										ceologinForm.setIsadmin(false);
										ceologinForm.setDeleted(false);
										ceologinForm.setKaarya(false);
										ceologinForm.setCompliance_one(false);
										ceologinForm.setCompliance_two(false);
										ceologinForm.setIs_compliance_one_access(true);
										ceologinForm.setPreferred_task_view('1');
										ceologinForm.setCompany_name(contactForm.getEmailId().replace("\"", "'"));
										ceologinForm.setPreferred_dashboard_view('0');
										
										List<String> returnStringList = loginService.signUpUser(ceologinForm);
										if(returnStringList.get(0).equals("success")){
											//send a mail to new user.
											String result = registerMail.sendRegisterMail(contactForm.getFirstName(), contactForm.getEmailId());
										}else if(returnStringList.get(0).equals("failure")){
											//delete a inserted contact.
											companymanagementservice.deleteContact(contactRefNo);
										}
								 }else { //activate is_compliance_access 
									   int result = loginService.activateIsComplianceAccess(newUser.getUserid());
									   log.info("User: "+newUser.getUserid() +" is activated is_compliance_access true");
								 }
							}	*/
						}else{
							log.error("Duplicate email id not allowed for same company employee");
						}
					}
					}
				} else { // if company exists then
					
					boolean isCompanyDeleted = companymanagementservice.getSingleCompanyForm(oldCoRefNo).isDeleted();
					if(isCompanyDeleted){

						map.addAttribute("old_company_id", oldCoRefNo);
						map.addAttribute("old_company_name", companymanagementservice.getSingleCompanyForm(oldCoRefNo).getCo_name());
						map.addAttribute("duplicate_name_warning", configReader.getMessage("COMPANY_EXISTS"));
					}else{
						
						map.addAttribute("error", configReader.getMessage("COMPANY_EXISTS"));
						
					}
					
				}
			}else{
				map.addAttribute("error", configReader.getMessage("COMPANY_NAME_EMPTY"));
			}
		}else{
			map.addAttribute("error", configReader.getMessage("COMPANY_CONTACT_ERROR"));
		}
		
		companyDetails = companymanagementservice
				.getCompanyList(user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
	    dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}
		
		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		return new ModelAndView("CompanyListView");
	}

	// add company contact
	@RequestMapping(value = CompanyManagementUriConstant.ADD_COMPANY_CONTACT, method = RequestMethod.POST,  headers = {"Content-type=application/json"})
	@ResponseBody
	public ModelAndView addCompanyContact(HttpServletRequest req,
			HttpServletResponse res, HttpSession session, ModelMap map, @RequestBody UpdateContactJson[] jsonList) throws Exception {
		
		int companyRefNo = Integer.parseInt(session.getAttribute("companyRefNo").toString());
		Date creatcompanydate = new Date();
		Timestamp companycontactdate = new Timestamp(creatcompanydate.getTime());
		String firstName = "", lastName = "", emailId = "";
		char contact_type_cd = '3';
		int contact_ref_no = 0 ;
		boolean isContactAlreadyAvailableWithSameMailId = true;
		LoginForm surrentloginForm = loginService.getUser(session.getAttribute("userId").toString());
		int user_ref_no = surrentloginForm.getId();
		
		for(UpdateContactJson json:jsonList){
			
			firstName = json.getFirstName().replace("\"", "'");
			lastName = json.getLastName().replace("\"", "'");
			emailId = json.getEmailId().replace("\"", "'");
			contact_type_cd = json.getDesignation().charAt(0);
			
			if(json.getContectRefNo() != null){ //update existing contact.
				
				contact_ref_no = Integer.parseInt(json.getContectRefNo());
				isContactAlreadyAvailableWithSameMailId = companymanagementservice.isEmployeeExistsWhileUpdating(contact_ref_no, companyRefNo, emailId);
				if(!isContactAlreadyAvailableWithSameMailId){

					if(contact_type_cd == '0'){
						map.addAttribute("error", configReader.getMessage("CONTACT_DESIGNATION_EMPTY"));
					}else if(firstName.length() == 0 && firstName.equals("") ){
						map.addAttribute("error", configReader.getMessage("CONTACT_FIRST_NAME_EMPTY"));					
					}else if(lastName.length() == 0 && lastName.equals("") ){
						map.addAttribute("error", configReader.getMessage("CONTACT_LAST_NAME_EMPTY"));					
					}else if(emailId.length() == 0 && emailId.equals("") ){
						map.addAttribute("error", configReader.getMessage("CONTACT_EMAIL_EMPTY"));					
					}else{
						int result = companymanagementservice.updateContactByFiled(contact_ref_no, firstName, lastName, emailId, contact_type_cd);
						if(result > 0){
							log.info("Record updated successfully");
						}else{
							log.error("Error occurred while updating record");
						}
					}
				}else{
					log.error("Duplicate email id not allowed for same company employee");
				}
			}else{
				
				if(!companymanagementservice.isEmployeeExists(companyRefNo, json.getEmailId())){
					//if another record with the same email id is not available
					
					if(contact_type_cd == '0'){
						map.addAttribute("error", configReader.getMessage("CONTACT_DESIGNATION_EMPTY"));
					}else if(firstName.length() == 0 && firstName.equals("") ){
						map.addAttribute("error", configReader.getMessage("CONTACT_FIRST_NAME_EMPTY"));					
					}else if(lastName.length() == 0 && lastName.equals("") ){
						map.addAttribute("error", configReader.getMessage("CONTACT_LAST_NAME_EMPTY"));					
					}else if(emailId.length() == 0 && emailId.equals("") ){
						map.addAttribute("error", configReader.getMessage("CONTACT_EMAIL_EMPTY"));					
					}else{			
						
						//insert a new contact with update time.
						ContactsForm contactform = new ContactsForm();
						contactform.setContact_type_cd(json.getDesignation().charAt(0));
						contactform.setCo_ref_no(companyRefNo);
						contactform.setContact_first_name(json.getFirstName().replace("\"", "'"));
						contactform.setContact_last_name(json.getLastName().replace("\"", "'"));
						contactform.setContact_mail_id(json.getEmailId().replace("\"", "'"));
						contactform.setCreate_ts(companycontactdate);
						contactform.setLast_update_ts(companycontactdate);
						contactform.setCreate_user_ref_no(user_ref_no);
						contactform.setLast_update_user_ref_no(user_ref_no);
						contactform.setOwner_user_ref_no(user_ref_no);
						int contactRefNo = companymanagementservice.addCompanyContacts(contactform);
						
						//create a compliance_one user for read only.
						if(contactRefNo > 0 && json.getDesignation().charAt(0) == '1'){		
								String newEmailId = json.getEmailId();
								LoginForm newUser = loginService.getUser(newEmailId);
								if(newUser == null){
									LoginForm loginForm = new LoginForm();
									loginForm.setUserid(json.getEmailId().replace("\"", "'"));
									loginForm.setPassword("");
									loginForm.setStatus(false);
									loginForm.setUsername(json.getFirstName().replace("\"", "'"));
									loginForm.setGarde(1);
									loginForm.setDepartment(1);
									loginForm.setIsadmin(false);
									loginForm.setDeleted(false);
									loginForm.setKaarya(false);
									loginForm.setCompliance_one(false);
									loginForm.setCompliance_two(false);
									loginForm.setIs_compliance_one_access(true);
									List<String> returnStringList = loginService.signUpUser(loginForm);
									if(returnStringList.get(0).equals("success")){							
										//send a mail to new user.
										String result = registerMail.sendRegisterMail(json.getFirstName(), json.getEmailId());
									}else if(returnStringList.get(0).equals("failure")){
										//delete a inserted contact.
										companymanagementservice.deleteContact(contactRefNo);
									}
								}else{
									   int result = loginService.activateIsComplianceAccess(newUser.getUserid());
									   log.info("User: "+newUser.getUserid() +" is activated is_compliance_access true");
								}
						}
					}
				}else{
					log.error("Duplicate email id not allowed for same company employee");
				}
			}
		}
				
		List<CompanyForm> companyDetails = companymanagementservice
				.getCompanyList(user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
	    List<String> dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}
		
		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		return new ModelAndView("CompanyListView");			
	}

	// list of company contact
	@RequestMapping(value = CompanyManagementUriConstant.LIST_OF_COMPANY_CONTACTS, method= RequestMethod.POST)
	public ModelAndView getCompanyContactList(ModelMap map,
			HttpServletRequest req, HttpServletResponse res, HttpSession session) throws JSONException, ParseException {
						
		LoginForm currentloginForm = loginService.getUser(session.getAttribute("userId").toString());
		String companyId = req.getParameter("companyId");
		session.setAttribute("companyRefNo", companyId);
		int user_ref_no = currentloginForm.getId();
		List<CompanyForm> companyDetails = companymanagementservice.getCompanyList(user_ref_no);
		List<DocumentsForm> attachedDocList = documentService.getDocListByCoRefNo(Integer.parseInt(companyId));
		List<String> dateList = new ArrayList<String>();
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
		for (CompanyForm form : companyDetails) {
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "
					+ calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}

		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		map.addAttribute("project_name", configReader.getValue("PROJECT_NAME"));
		map.addAttribute("username", session.getAttribute("username"));
		map.addAttribute("projectAvailble", session.getAttribute("projectAvailble"));

		Integer ownerUserRef = Integer.parseInt(companyId);
		Integer coRefNo = ownerUserRef;

		List<ContactsForm> contactDetails = companymanagementservice.getContactList(coRefNo);
		List<Boolean> isDeletedList = new ArrayList<Boolean>();
		for (ContactsForm form : contactDetails) {
			int company_ref_no = form.getCo_ref_no();
			int contact_ref_no = form.getContact_ref_no();
			List<CompanyComplianceRelationForm> relForm = companyCompliance.getContacts(company_ref_no, contact_ref_no);
			if (relForm != null && relForm.size() == 0) {
				isDeletedList.add(true);
			} else {
				isDeletedList.add(false);
			}
		}

		map.addAttribute("isDeletedList", isDeletedList);
		map.addAttribute("contactDetails", contactDetails);
		map.addAttribute("co_ref_no", companyId);
		map.addAttribute("attachedDocList", rulesService.getDesignedDocList(attachedDocList));
		return new ModelAndView("UpdateContactTemplate");
	}

	// request for company management dashboard page.
	@RequestMapping(value = CompanyManagementUriConstant.COMPANY_MANAGEMENT)
	public ModelAndView companyManagement(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, ModelMap map) {

		LoginForm currentloginForm = loginService.getUser(session.getAttribute("userId").toString());
		int user_ref_no = currentloginForm.getId();
		List<String> projectAvailble = new ArrayList<String>();
		if(currentloginForm.getKaarya()){
			projectAvailble.add(AppConstant.KAARYA);
		}
		List<CompanyForm> companyDetails = companymanagementservice
				.getCompanyList(user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
		List<String> dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}

		map.addAttribute("_csrf_token", session.getAttribute("csrf_token").toString());			
		map.addAttribute("Isadmin", currentloginForm.getIsadmin());
		map.addAttribute("isKaaryaAccess", currentloginForm.getKaarya());		
		map.addAttribute("isComplianceOneAccess", currentloginForm.getCompliance_one());
        map.addAttribute("isAddonOneAccess", currentloginForm.getIs_compliance_one_access());
		map.addAttribute("dateList", dateList);		
		map.addAttribute("companyDetails", companyDetails);
		map.addAttribute("project_name", configReader.getValue("PROJECT_NAME"));
		map.addAttribute("username", currentloginForm.getUsername());
		map.addAttribute("projectAvailble", projectAvailble);
		map.addAttribute("noOfUserPerCompany", configReader.getValue("NO_OF_USER_PER_COMPANY"));
		return new ModelAndView("CompanyManagement");
	}

	/* Add more template */
	@RequestMapping(value = CompanyManagementUriConstant.ADD_MORE_TEMPLATE  )
	public ModelAndView loadContactTemplate(){
		return new ModelAndView("AddMoreContactTemplate");
	}
	
	/* Add more contact template for registered company*/
	@RequestMapping(value = CompanyManagementUriConstant.ADD_MORE_COMPANY_CONTACT_TEMPLATE)
	public ModelAndView loadCompanyContactTemplate(){
		return new ModelAndView("ContactTemplate");
	}
	
	//delete contact details by contact_ref_no
	@RequestMapping(value = CompanyManagementUriConstant.DELETE_CONTACT_BY_ID, method = RequestMethod.POST)
	@ResponseBody
	public String deleteContactById(HttpServletRequest req, HttpServletResponse res, HttpSession session){

		JSONObject obj = new JSONObject();
		String contactRefNo = req.getParameter("contactId");	
		ContactsForm contactForm = companymanagementservice.getSingleContactFormByRefNo(Integer.parseInt(contactRefNo));
		String userId = contactForm.getContact_mail_id();
		LoginForm newLoginForm = loginService.getUser(userId);
		int response = companymanagementservice.deleteContact(Integer.parseInt(contactRefNo));
		if(response > 0){
			
            obj.put("success", "Record deleted successfully");
            log.info("Contact user deleted form Contact table");
            if(newLoginForm != null && newLoginForm.getId() > 0 && !newLoginForm.getKaarya() 
            		&& !newLoginForm.getCompliance_one() && newLoginForm.getIs_compliance_one_access()){
            	
            	int result = loginService.deleteUserByEmailId(userId);
            	if(result > 0){
            		log.info("User deleted from Login Table too");
            	}
            }else{
            	log.info("User can't be deleted from User Table");
            }
		}
	    else{
	        obj.put("failure", "Record deletion failed");
	    }
/*		if(contactForm.getContact_type_cd() == '1'){
			int result = loginService.deleteUserByEmailId(userId);
		}else{
			int response = companymanagementservice.deleteContact(Integer.parseInt(contactRefNo));
			if(response>0){
	            obj.put("success", "Record deleted successfully");
	            loginService.deleteUserByEmailId(contactForm.getContact_mail_id());
			}
		    else{
		        obj.put("failure", "Record deletion failed");
		    }
		}*/
		
		return obj.toString();		
	}
	
	//update contact by contact_ref_no
	@RequestMapping(value = CompanyManagementUriConstant.UPDATE_CONTACT_DETAIL, method = RequestMethod.POST)
	public String updateCompanyContact(HttpServletRequest req, HttpServletResponse res, HttpSession session){
		
		LoginForm currentloginForm = loginService.getUser(session.getAttribute("userId").toString());
		int user_ref_no = currentloginForm.getId();
		Date creatcompanycontactdate = new Date();
		Timestamp companycontactdate = new Timestamp(
				creatcompanycontactdate.getTime());
		
		String companyRefID = req.getParameter("companyRefID");
		String contactTypeCd = req.getParameter("contactTypeCd");
		String contactFirstName = req.getParameter("firstName").replace("\"", "'");
		String contactLastName = req.getParameter("lastName").replace("\"", "'");
		String contactMailId = req.getParameter("emailId").replace("\"", "'");
		
		
		ContactsForm contactform = new ContactsForm();
		contactform.setContact_ref_no(Integer.parseInt(companyRefID));
		contactform.setContact_type_cd(contactTypeCd.charAt(0));
		contactform.setContact_first_name(contactFirstName);
		contactform.setContact_last_name(contactLastName);
		contactform.setContact_mail_id(contactMailId);
		contactform.setLast_update_ts(companycontactdate);
		contactform.setLast_update_user_ref_no(user_ref_no);
		contactform.setOwner_user_ref_no(user_ref_no);
		
		
		Integer response = companymanagementservice.updateCompanyContact(contactform);		
		JSONObject obj = new JSONObject();		
		return obj.toString();
	}
	
	//function to get ceo of a particular company
	@RequestMapping(value = CompanyManagementUriConstant.GET_COMPANY_CEO)
	@ResponseBody
	public String getCeoOfCompany(@PathVariable("co_ref_no") int co_ref_no, HttpServletRequest req, HttpServletResponse res, HttpSession session){
		
		char contact_type_cd = '1';
		JSONObject mainObj = new JSONObject();
		ContactsForm newContactsForm = companymanagementservice.getCeoOfCompany(co_ref_no, contact_type_cd); 
		if(newContactsForm != null && newContactsForm.getContact_ref_no() > 0){
			mainObj = new JSONObject(newContactsForm);
		}
		else{
			mainObj.put("CEO_NOT_FOUND", "No ceo has been assigned to this company.");
		}
		return mainObj.toString();
	}
	
	@RequestMapping(value = CompanyManagementUriConstant.DELETE_COMPANY)
	public ModelAndView deleteCompany(ModelMap map, @PathVariable("co_ref_no") int co_ref_no, HttpServletRequest req, HttpServletResponse res, HttpSession session){
		
		log.info("Request came to delete company");
		Date todayDate = new Date();
		int noOfUpdatedRows = 0;
		boolean deleteAllowed = true, isDeleted = true;
		JSONObject mainObj = new JSONObject();
		int current_user_ref_no = Integer.parseInt(session.getAttribute("user_ref_no").toString());
		ComplianceItemsForm newComplianceItemsForm = new ComplianceItemsForm();
		List<CompanyComplianceRelationForm> companyComplianceList = companyCompliance.getCompanyComplianceRelationBasedOnComp(co_ref_no);
		
		for(CompanyComplianceRelationForm newCompanyComplianceRelationForm : companyComplianceList){
			
			newComplianceItemsForm = rulesService.getRule(newCompanyComplianceRelationForm.getComp_item_ref_no());
			if(newComplianceItemsForm.getFrequency() == '7'){
				int compareDate = todayDate.compareTo(newComplianceItemsForm.getRule_start_dt());
				if(compareDate > 0){	//Rule has already executed so it is allowed to delete
					deleteAllowed = true;
				}else{	//Rule has not been executed so it can't be deleted
					deleteAllowed = false;
					break;
				}								
			}else{
				deleteAllowed = false;
				break;
			}
		}
		if(deleteAllowed){	
			noOfUpdatedRows = companymanagementservice.changeCompanyStatus(co_ref_no, isDeleted, current_user_ref_no);
			if(noOfUpdatedRows > 0){
				log.info("Company status has been marked deleted successfully");
				map.addAttribute("error", configReader.getMessage("COMPANY_DELETED_SUCCESSFULLY"));
			}else{
				log.info("Error while changing Company status to delete");
				map.addAttribute("error", configReader.getMessage("COMPANY_DELETED_UNSUCCESSFULLY"));
			}
		}else{	
			map.addAttribute("error", configReader.getMessage("COMPANY_DELETED_UNSUCCESSFULLY"));
		}
		List<CompanyForm> companyDetails = companymanagementservice.getCompanyList(current_user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
		List<String> dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}
		
		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		return new ModelAndView("CompanyListView");
	}
	
	@RequestMapping(value = CompanyManagementUriConstant.RESTORE_COMPANY)
	public ModelAndView restoreCompany(ModelMap map, @PathVariable("co_ref_no") int co_ref_no, HttpServletRequest req, HttpServletResponse res, HttpSession session){
		
		log.info("Request came to restore company");
		int noOfUpdatedRows = 0;
		boolean isDeleted = false;
		int current_user_ref_no = Integer.parseInt(session.getAttribute("user_ref_no").toString());
		boolean isOwner = companymanagementservice.isOwner(co_ref_no, current_user_ref_no);

		if(isOwner){	
			noOfUpdatedRows = companymanagementservice.changeCompanyStatus(co_ref_no, isDeleted, current_user_ref_no);
			if(noOfUpdatedRows > 0){
				log.info("Company status has been marked as not deleted successfully");
				log.info(configReader.getMessage("COMPANY_DELETED_SUCCESSFULLY"));
			}else{
				log.info("Error while changing Company status to delete");
				log.error(configReader.getMessage("COMPANY_DELETED_UNSUCCESSFULLY"));
			}
		}else{	
			log.error("Not authorized to restore company");
		}
		List<CompanyForm> companyDetails = companymanagementservice.getCompanyList(current_user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
		List<String> dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}
		
		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		return new ModelAndView("CompanyListView");
	}
	
	@RequestMapping(value = CompanyManagementUriConstant.REFRESH_PAGE)
	public ModelAndView refreshPage(ModelMap map, HttpServletRequest req, HttpServletResponse res, HttpSession session){
		
		log.info("Request came to refresh page");
		int current_user_ref_no = Integer.parseInt(session.getAttribute("user_ref_no").toString());
		List<CompanyForm> companyDetails = companymanagementservice.getCompanyList(current_user_ref_no);
		
		if(companyDetails.size() > 0){
			Collections.sort(companyDetails);
		}
		
		List<String> dateList = new ArrayList<String>();
		
		for(CompanyForm form: companyDetails){	
			Timestamp t = form.getCreate_ts();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(t.getTime());			
			String dateStr = dateFormatter.getMonthName(String.valueOf(calendar.get(Calendar.MONTH))) + " "+calendar.get(Calendar.DATE);
			dateList.add(dateStr);
		}
		
		map.addAttribute("dateList", dateList);
		map.addAttribute("companyDetails", companyDetails);
		return new ModelAndView("CompanyListView");
	}
	
}
