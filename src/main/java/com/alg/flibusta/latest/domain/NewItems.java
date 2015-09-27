package com.alg.flibusta.latest.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "flibusta_latest")
public class NewItems {

    /**
     * ���������
     */
    @Column(name = "updated_Date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private Date updated;

    /**
     * flibusta book id
     */
    @NotNull
    @Column(name = "id_tagBook")
    private Integer idTagBook;

    /**
     * �������� �����
     */
    @NotNull
    @Column(name = "title")
    private String title;

    /**
     * ����� �����
     */
    @NotNull
    @Column(name = "author")
    private String author;

    /**
     * ���������
     */
    @NotNull
    @Column(name = "categories")
    private String categories;

    /**
     * ����������
     */
    @NotNull
    @Column(name = "content")
    private String content;
}
