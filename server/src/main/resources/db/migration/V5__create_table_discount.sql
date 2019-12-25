create table discount
  (
      id varchar(255) not null
          primary key,
      start_date date not null,
      end_date date not null,
      entity_id varchar(255) not null,
      entity_type varchar(255) not null,
      rate double not null,
      constraint UK_START_END_DATE_ENTITY_ID_TYPE
          unique (start_date, end_date, entity_id, entity_type)
  );
