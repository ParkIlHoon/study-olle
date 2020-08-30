package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})
@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.only", attributeNodes = {
        @NamedAttributeNode("managers")
})
public class Study
{
    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting = false;

    private boolean published = false;

    private boolean closed = false;

    private boolean useBanner = false;

    public void addManager(Account account)
    {
        this.managers.add(account);
    }

    public String getEncodePath()
    {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public boolean isJoinable(UserAccount account)
    {
        return (published && recruiting && !closed) && !members.contains(account.getAccount()) && !managers.contains(account.getAccount());
    }

    public boolean isManager(UserAccount account)
    {
        return managers.contains(account.getAccount());
    }

    public boolean isMember(UserAccount account)
    {
        return members.contains(account.getAccount());
    }

    public void publish()
    {
        this.published = true;
        this.publishedDateTime = LocalDateTime.now();
    }

    public void close()
    {
        this.closed = true;
        this.recruiting = false;
        this.closedDateTime = LocalDateTime.now();
    }

    public boolean isRecruitUpdatable()
    {
        return this.recruitingUpdatedDateTime == null || LocalDateTime.now().isAfter(this.recruitingUpdatedDateTime.plusHours(1));
    }

    public void startRecruiting()
    {
        this.recruiting = true;
        this.recruitingUpdatedDateTime = LocalDateTime.now();
    }

    public void stopRecruiting()
    {
        this.recruiting = false;
        this.recruitingUpdatedDateTime = LocalDateTime.now();
    }

    public boolean isRemovable()
    {
        return this.closed;
    }
}
