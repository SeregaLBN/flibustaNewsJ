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
import javax.validation.constraints.Size;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(table = "flibusta_latest")
public class NewItems {

    /**
     * Added to flibusta
     */
    @Column(name = "updated_Date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy.MM.dd hh:mm:ss")
    private Date updated;

    /**
     * flibusta book id
     */
    @NotNull
    @Column(name = "id_tag_Book")
    private Integer idTagBook;

    /**
     * Book title
     */
    @NotNull
    @Column(name = "title")
    private String title;

    /**
     * Book author
     */
    @Column(name = "author")
    private String author;

    /**
     * Categories
     */
    @NotNull
    @Column(name = "categories")
    @Size(max = 999)
    private String categories;

    /**
     * Content description
     */
    @NotNull
    @Column(name = "content")
    @Size(max = 4000)
    private String content;
}
