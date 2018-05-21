package com.platformia.bringsouvenir.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.platformia.bringsouvenir.dao.GenericHibernateDao;
import com.platformia.bringsouvenir.entity.Account;
import com.platformia.bringsouvenir.exception.ApiError;
import com.platformia.bringsouvenir.form.AccountForm;
import com.platformia.bringsouvenir.security.SecurityUtils;
import com.platformia.bringsouvenir.validator.AccountValidator;

@RestController
public class AccountRESTController {

	@Autowired
	private GenericHibernateDao<Account> accountDAO;

	// initiate DAO Classes
	@PostConstruct
	public void init() {
		accountDAO.setEntityClass(Account.class);
	}

	@Autowired
	private AccountValidator accountValidator;

	// Set a form validator
	@InitBinder
	protected void initBinder(WebDataBinder dataBinder) {
		// Form target
		Object target = dataBinder.getTarget();
		if (target == null) {
			return;
		}
		System.out.println("Target=" + target);

		if (target.getClass() == AccountForm.class) {
			dataBinder.setValidator(accountValidator);
		}
	}

	// CREATE URL : /account or /account.xml or /account.json
	@RequestMapping(value = "/account/seller", //
			method = RequestMethod.POST, //
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public ApiError addAccount(@Validated @RequestBody AccountForm accountForm, BindingResult result) {

		HttpStatus httpStatus;
		String message = "default message de mierda";
		List<String> errors = new ArrayList<String>();
		
		// Validate result
		if (!result.hasErrors()) {
			System.out.println("(Service Side) Creating 	account with userName: " + accountForm.getUserName());
			Account newAccount = new Account(accountForm, Account.ROLE_SELLER);
			accountDAO.create(newAccount);
			// Send email link for email validation
			// @sendValidationemail
			//---
			//send confirmation
			httpStatus = HttpStatus.OK;
			message = "Account with userName : " + accountForm.getUserName() + " successfully created! " ;
			
		} else {
			//send back list of errors
			httpStatus = HttpStatus.BAD_REQUEST;
			message = "Review your request please !" ;
			for (FieldError error : result.getFieldErrors()) {
		        errors.add(error.getField() + ": " + error.getCode());
		    }
		}
		return new ApiError(httpStatus, message, errors);
	}

	// EDIT URL : /account or /account.xml or /account.json
	@RequestMapping(value = "/account", //
			method = RequestMethod.PUT, //
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public ApiError updateAccount(@Validated @RequestBody AccountForm accountForm) {

		System.out.println("(Service Side) Editing password for account with userName: " + accountForm.getUserName());
		Account account = accountDAO.findOne(accountForm.getUserName());
		if (account != null) {
			account.setEncrytedPassword(SecurityUtils.encrytePassword(accountForm.getPassword()));
			accountDAO.update(account);
		} else {
			System.out.println("(Service Side) account with userName: " + accountForm.getUserName() + "  not found");

		}
		return null;
	}

}
