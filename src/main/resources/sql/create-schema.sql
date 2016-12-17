create sequence SEQ_DOMINION start 1 increment 1;

    create table Domain (
        id int8 not null,
        version int8,
        description varchar(255),
        name varchar(255),
        updateTime timestamp,
        tld_id int8,
        primary key (id)
    );

    create table Domain_Keyword (
        Domain_id int8 not null,
        keywords_id int8 not null
    );

    create table Keyword (
        id int8 not null,
        version int8,
        name varchar(255),
        usage int4 not null,
        primary key (id)
    );

    create table Tld (
        id int8 not null,
        version int8,
        name varchar(255),
        organisation varchar(255),
        type varchar(255),
        usage int4 not null,
        primary key (id)
    );

    alter table Domain 
        add constraint UKr2vfj343j0ua4v1v6db8u2bk8 unique (name, tld_id);

    alter table Domain_Keyword 
        add constraint UK_31qdl26vo521vlmajoi7bet62 unique (keywords_id);

    alter table Tld 
        add constraint UK_loq98s4rbq4kcg75eexgwe8oq unique (name);

    alter table Domain 
        add constraint FK20bj4t3p8puhglb8e67ahw409 
        foreign key (tld_id) 
        references Tld;

    alter table Domain_Keyword 
        add constraint FKhjayeuihqvo04fa8n65n5xlne 
        foreign key (keywords_id) 
        references Keyword;

    alter table Domain_Keyword 
        add constraint FKgsuhnjn36ttw27dcy4kuf7nn8 
        foreign key (Domain_id) 
        references Domain;