package com.davivienda.factoraje.domain.dto;

import java.util.Set;
import java.util.UUID;

import com.davivienda.factoraje.domain.model.UserModel;

public class SupplierDTOResponse {

    private UUID supplierId;
    private String name;
    private String code;
    private String nit;
    private String email;
    private String bankAccount;
    private Set<UserModel> users;

    public SupplierDTOResponse() {
    }
    public SupplierDTOResponse(UUID supplierId, String name, String code, String nit, String email, String bankAccount, Set<UserModel> users) {
        this.supplierId = supplierId;
        this.name = name;
        this.code = code;
        this.nit = nit;
        this.email = email;
        this.bankAccount = bankAccount;
    }   

    public UUID getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getNit() {
        return nit;
    }
    public void setNit(String nit) {
        this.nit = nit;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    public Set<UserModel> getUsers() {
        return users;
    }
    public void setUsers(Set<UserModel> users) {
        this.users = users; 
    }
    
}
