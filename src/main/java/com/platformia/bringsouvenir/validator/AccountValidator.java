package com.platformia.bringsouvenir.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.platformia.bringsouvenir.dao.GenericHibernateDao;
import com.platformia.bringsouvenir.entity.Account;
import com.platformia.bringsouvenir.form.AccountForm;

@Component
public class AccountValidator implements Validator {

	// common-validator library.
	private EmailValidator emailValidator = EmailValidator.getInstance();

	@Autowired
	private GenericHibernateDao<Account> accountDAO;

	// The classes are supported by this validator.
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == AccountForm.class;
	}

	@Override
	public void validate(Object target, Errors errors) {
		AccountForm accountForm = (AccountForm) target;

		// Check the fields of AccountForm.
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.accountForm.email");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.accountForm.password");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.accountForm.confirmPassword");

		if (!this.emailValidator.isValid(accountForm.getUserName())) {
			// Invalid email.
			errors.rejectValue("userName", "Pattern.accountForm.email");
		} else if (accountForm.getUserName() != null) {
			accountDAO.setEntityClass(Account.class);
			Account dbAccount = accountDAO.findOne(accountForm.getUserName());
			if (dbAccount != null) {
				// UserName has been used by another account.
				errors.rejectValue("userName", "Duplicate.accountForm.userName");
			}
		}

		// matching password and its confirmation
		if (!errors.hasErrors()) {
			if (!accountForm.getConfirmPassword().equals(accountForm.getPassword())) {
				errors.rejectValue("confirmPassword", "Match.accountForm.confirmPassword");
			}
		}
	}

}