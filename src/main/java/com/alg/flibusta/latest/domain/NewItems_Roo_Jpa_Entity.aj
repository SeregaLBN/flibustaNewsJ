// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.alg.flibusta.latest.domain;

import com.alg.flibusta.latest.domain.NewItems;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

privileged aspect NewItems_Roo_Jpa_Entity {
    
    declare @type: NewItems: @Entity;
    
    declare @type: NewItems: @Table(name = "flibusta_latest");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long NewItems.id;
    
    @Version
    @Column(name = "version")
    private Integer NewItems.version;
    
    public Long NewItems.getId() {
        return this.id;
    }
    
    public void NewItems.setId(Long id) {
        this.id = id;
    }
    
    public Integer NewItems.getVersion() {
        return this.version;
    }
    
    public void NewItems.setVersion(Integer version) {
        this.version = version;
    }
    
}
