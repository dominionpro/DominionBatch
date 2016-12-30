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
        
    create table Subdomain (
        id int8 not null,
        version int8,
        description varchar(255),
        name varchar(255),
        redirectContext varchar(255),
        title varchar(255),
        updateTime timestamp,
        domain_id int8,
        primary key (id)
    );

    create table Subdomain_Keyword (
        Subdomain_id int8 not null,
        keywords_id int8 not null
    );
    
    alter table Subdomain_Keyword 
        add constraint UK_3sol0r64sumru4mx3ne3kakx6 unique (keywords_id);
        
    alter table Subdomain 
        add constraint FKs6rv3lg53krmkntshk6wkp3o2 
        foreign key (domain_id) 
        references Domain;

    alter table Subdomain_Keyword 
        add constraint FKp5veile0vnlimdtx97q78hhie 
        foreign key (keywords_id) 
        references Keyword;

    alter table Subdomain_Keyword 
        add constraint FKg5yy7uvbaxpodu6xw4vnu65pj 
        foreign key (Subdomain_id) 
        references Subdomain;
        
    alter table Domain add column
 		redirectContext varchar(255);
	alter table Domain add column
 		title varchar(255);
	alter table Domain add column
 		redirectSubdomain_id int8;

	alter table Domain 
		add constraint FKatda1fi4uwqrgo3uxbh63dm1h 
		foreign key (redirectSubdomain_id) 
		references Subdomain;




