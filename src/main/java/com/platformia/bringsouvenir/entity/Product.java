package com.platformia.bringsouvenir.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.platformia.bringsouvenir.form.ProductForm;

@Entity
@Table(name = "Products")
@FilterDef(name="authorizer", parameters={@ParamDef( name="userName", type="string")})
@Filter(name = "authorizer" ,condition = "id in (select x.id from Account_Product x where x.user_name=:userName)")
public class Product implements Serializable {

	private static final long serialVersionUID = -4317973666304116002L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    
    @Column(name = "price", nullable = false)
    private int price;
    
    @ManyToMany(mappedBy = "products")
    private Set<Account> accounts;
    
	public Product(ProductForm productForm) {
		this.name = productForm.getName();
		this.price = productForm.getPrice();
	}
	
	//GETTERS SETTERS
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
}
