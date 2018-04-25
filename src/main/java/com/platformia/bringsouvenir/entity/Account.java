package com.platformia.bringsouvenir.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.platformia.bringsouvenir.form.AccountForm;
import com.platformia.bringsouvenir.security.SecurityUtils;


@Entity
@Table(name = "Accounts")
public class Account implements Serializable {

	private static final long serialVersionUID = 7377377702416850270L;

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SELLER = "ROLE_SELLER";
	public static final String ROLE_CLIENT = "ROLE_CLIENT";
	
    @Id
    @Column(name = "user_name", length = 20, nullable = false)
    private String userName;
 
    @Column(name = "encryted_password", length = 128, nullable = false)
    private String encrytedPassword;
     
    @Column(name = "active", length = 1, nullable = false, columnDefinition = "TINYINT(1)")
    private boolean active;
 
    @Column(name = "user_role", length = 20, nullable = false)
    private String userRole;
    
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "Account_Product", 
        joinColumns = { @JoinColumn(name = "user_name") }, 
        inverseJoinColumns = { @JoinColumn(name = "id") })
    Set<Product> products = new HashSet<>();
	
    public Account() {

	}
        
	public Account(String userName, String encrytedPassword, boolean active, String userRole, Set<Product> products) {
		this.userName = userName;
		this.encrytedPassword = encrytedPassword;
		this.active = active;
		this.userRole = userRole;
		this.products = products;
	}

	public Account(AccountForm accountForm, String role) {
		this.userName = accountForm.getUserName();
		this.encrytedPassword = SecurityUtils.encrytePassword(accountForm.getPassword());
		this.active = false;
		this.userRole = role;
	}

	//GETTERS SETTERS

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEncrytedPassword() {
		return encrytedPassword;
	}

	public void setEncrytedPassword(String encrytedPassword) {
		this.encrytedPassword = encrytedPassword;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}


	public Set<Product> getProducts() {
		return products;
	}


	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	
}
