--????????
insert into module_list(moduleid,modulename,rightid,action,cmid,roletype)
values('3020210','????????,'1','../supsettle/search.jsp',0,0);

--????????
insert into module_list(moduleid,modulename,rightid,action,cmid,roletype)
values('3020211','????????,'1','../suppay/search.jsp',0,0);

--???????????
insert into module_list(moduleid,modulename,rightid,action,cmid,roletype)
values('3020212','???????????,'1','../venderinvoice/search.jsp',0,0);
--???????????
insert into module_list(moduleid,modulename,rightid,action,cmid,roletype)
values('3020213','??????????????,'1','../supsettle/search_list.jsp',0,0);

create table DBUSRVSS.FIN_STOPCASH
(
  CODE     CHAR(2) not null,
  STOPNAME VARCHAR2(100) not null,
  DJLB     CHAR(1) not null,
  STATUS   VARCHAR2(2) default 'Y' not null,
  MEMO     VARCHAR2(10)
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_STOPCASH
  is '??????';
comment on column DBUSRVSS.FIN_STOPCASH.CODE
  is '???';
comment on column DBUSRVSS.FIN_STOPCASH.STOPNAME
  is '??????';
comment on column DBUSRVSS.FIN_STOPCASH.DJLB
  is '????????-???????-??????';
comment on column DBUSRVSS.FIN_STOPCASH.STATUS
  is '????;
alter table DBUSRVSS.FIN_STOPCASH
  add constraint PK_FIN_STOPCASH primary key (CODE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPINVOICE
prompt =============================
prompt
create table DBUSRVSS.FIN_SUPINVOICE
(
  BILLNO     VARCHAR2(20) not null,
  HANDNO     VARCHAR2(20) not null,
  BTYPE      CHAR(3) not null,
  FLAG       CHAR(1) not null,
  SADATE     DATE not null,
  MRID       VARCHAR2(20) not null,
  MAID       VARCHAR2(20),
  PCID       VARCHAR2(30),
  SUPID      VARCHAR2(8),
  WMID       CHAR(1),
  CONTNO     VARCHAR2(15),
  FPMONEY    NUMBER(14,2),
  FPMONEYBHS NUMBER(14,2) default 0,
  FPLX       CHAR(1),
  FPSL       NUMBER(3,2),
  INPUTOR    VARCHAR2(15) not null,
  INPUTDATE  DATE not null,
  AUDITOR    VARCHAR2(15),
  AUDITDATE  DATE,
  PERSON1    VARCHAR2(15),
  PERSON2    VARCHAR2(15),
  PERSON3    VARCHAR2(20),
  PERSON4    VARCHAR2(15),
  PERSON5    VARCHAR2(15),
  MEMO       VARCHAR2(400),
  D1         DATE,
  FPSE       NUMBER(14,2),
  MON        CHAR(6),
  SHEETTYPE  CHAR(1) not null
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPINVOICE
  is '[SID]???????????;
comment on column DBUSRVSS.FIN_SUPINVOICE.BILLNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.HANDNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.BTYPE
  is '????????16??;
comment on column DBUSRVSS.FIN_SUPINVOICE.FLAG
  is '???????? N/???  W/?????/????? E/??;
comment on column DBUSRVSS.FIN_SUPINVOICE.SADATE
  is '???????;
comment on column DBUSRVSS.FIN_SUPINVOICE.MRID
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.MAID
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.PCID
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.SUPID
  is '?????;
comment on column DBUSRVSS.FIN_SUPINVOICE.WMID
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.CONTNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.FPMONEY
  is '?????????';
comment on column DBUSRVSS.FIN_SUPINVOICE.FPMONEYBHS
  is '???????????;
comment on column DBUSRVSS.FIN_SUPINVOICE.FPLX
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.FPSL
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.INPUTOR
  is '?????;
comment on column DBUSRVSS.FIN_SUPINVOICE.INPUTDATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.AUDITOR
  is '?????;
comment on column DBUSRVSS.FIN_SUPINVOICE.AUDITDATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.PERSON1
  is 'PERSON1';
comment on column DBUSRVSS.FIN_SUPINVOICE.PERSON2
  is 'PERSON2';
comment on column DBUSRVSS.FIN_SUPINVOICE.PERSON3
  is 'PERSON3';
comment on column DBUSRVSS.FIN_SUPINVOICE.PERSON4
  is 'PERSON4';
comment on column DBUSRVSS.FIN_SUPINVOICE.PERSON5
  is 'PERSON5';
comment on column DBUSRVSS.FIN_SUPINVOICE.MEMO
  is '???';
comment on column DBUSRVSS.FIN_SUPINVOICE.D1
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.FPSE
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE.MON
  is '??????';
alter table DBUSRVSS.FIN_SUPINVOICE
  add constraint PK_FIN_SUPINVOICE primary key (BILLNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create unique index DBUSRVSS.IDX_FIN_SUPINVOICE_HANDNO on DBUSRVSS.FIN_SUPINVOICE (HANDNO)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPINVOICE_SETTLENO on DBUSRVSS.FIN_SUPINVOICE (AUDITDATE)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPINVOICE_SUPID on DBUSRVSS.FIN_SUPINVOICE (SUPID, WMID, PCID)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPINVOICE_LOG
prompt =================================
prompt
create table DBUSRVSS.FIN_SUPINVOICE_LOG
(
  BILLNO    VARCHAR2(20) not null,
  HANDNO    VARCHAR2(20) not null,
  SADATE    DATE not null,
  FPSL      NUMBER(3,2) not null,
  FLAG      INTEGER default 0 not null,
  TOUCHER   VARCHAR2(20) not null,
  TOUCHTIME DATE default sysdate not null,
  SHEETTYPE CHAR(1) not null,
  NOTE      VARCHAR2(200)
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.BILLNO
  is '????????;
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.HANDNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.SADATE
  is '???????;
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.FPSL
  is '??????';
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.FLAG
  is '???????0??? 1????????100 ??????????????;
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.TOUCHER
  is '????????D';
comment on column DBUSRVSS.FIN_SUPINVOICE_LOG.TOUCHTIME
  is '???????????;
alter table DBUSRVSS.FIN_SUPINVOICE_LOG
  add constraint PK_FIN_SUPINVOICE_LOG primary key (BILLNO, SHEETTYPE)
  using index 
  tablespace DATA_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create bitmap index DBUSRVSS.IDX_INVOICE_LOG_FLAG on DBUSRVSS.FIN_SUPINVOICE_LOG (FLAG)
  tablespace DATA_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPPAY_FP
prompt ============================
prompt
create table DBUSRVSS.FIN_SUPPAY_FP
(
  BILLNO    VARCHAR2(20) not null,
  ROWNO     NUMBER(6) not null,
  FPNO      VARCHAR2(20),
  MRID      VARCHAR2(20),
  MAID      VARCHAR2(20),
  WMID      CHAR(1),
  FPHANDNO  VARCHAR2(20),
  FPJE      NUMBER(14,2) default 0,
  FPBHSJE   NUMBER(14,2) default 0,
  FPLX      CHAR(1),
  FPSL      NUMBER(3,2),
  AUDITOR   VARCHAR2(15),
  AUDITDATE DATE,
  MEMO      VARCHAR2(400),
  SHEETTYPE CHAR(1) not null
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPPAY_FP
  is '[SFD]??????????????;
comment on column DBUSRVSS.FIN_SUPPAY_FP.BILLNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.ROWNO
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_FP.FPNO
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.MRID
  is '???/???';
comment on column DBUSRVSS.FIN_SUPPAY_FP.MAID
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_FP.WMID
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.FPHANDNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.FPJE
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.FPBHSJE
  is '???????????;
comment on column DBUSRVSS.FIN_SUPPAY_FP.FPLX
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.FPSL
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.AUDITOR
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_FP.AUDITDATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_FP.MEMO
  is '???';
alter table DBUSRVSS.FIN_SUPPAY_FP
  add constraint PK_FIN_SUPPAY_FP primary key (BILLNO, ROWNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPPAY_ID
prompt ============================
prompt
create table DBUSRVSS.FIN_SUPPAY_ID
(
  BILLNO    VARCHAR2(20) not null,
  ROWNO     NUMBER(6) not null,
  SETTLENO  VARCHAR2(20),
  MRID      VARCHAR2(20),
  MAID      VARCHAR2(20),
  WMID      CHAR(1),
  JHJE      NUMBER(14,2) default 0,
  JHTZ      NUMBER(14,2) default 0,
  XSJE      NUMBER(14,2) default 0,
  XSTZ      NUMBER(14,2) default 0,
  FYJE      NUMBER(14,2) default 0,
  ZKJE      NUMBER(14,2) default 0,
  CYTZ      NUMBER(14,2) default 0,
  YFJE      NUMBER(14,2) default 0,
  SFJE      NUMBER(14,2) default 0,
  AUDITOR   VARCHAR2(15),
  AUDITDATE DATE,
  MEMO      DATE,
  SHEETTYPE CHAR(1) not null
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPPAY_ID
  is '[SPD]??????????????;
comment on column DBUSRVSS.FIN_SUPPAY_ID.BILLNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.ROWNO
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_ID.SETTLENO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.MRID
  is '???/???';
comment on column DBUSRVSS.FIN_SUPPAY_ID.MAID
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_ID.WMID
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.JHJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.JHTZ
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.XSJE
  is '???????;
comment on column DBUSRVSS.FIN_SUPPAY_ID.XSTZ
  is '???????;
comment on column DBUSRVSS.FIN_SUPPAY_ID.FYJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.ZKJE
  is '????????;
comment on column DBUSRVSS.FIN_SUPPAY_ID.CYTZ
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.YFJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.SFJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.AUDITOR
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_ID.AUDITDATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_ID.MEMO
  is '??????';
alter table DBUSRVSS.FIN_SUPPAY_ID
  add constraint PK_FIN_SUPPAY_ID primary key (BILLNO, ROWNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPPAY_ID_FKSH on DBUSRVSS.FIN_SUPPAY_ID (SETTLENO)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPPAY_IH
prompt ============================
prompt
create table DBUSRVSS.FIN_SUPPAY_IH
(
  BILLNO     VARCHAR2(20) not null,
  HANDNO     VARCHAR2(20) not null,
  BTYPE      CHAR(3) not null,
  FLAG       CHAR(1) not null,
  BSOURCE    CHAR(1) not null,
  PCID       VARCHAR2(20) not null,
  MRID       VARCHAR2(20),
  SUPID      VARCHAR2(8) not null,
  CONTNO     VARCHAR2(20),
  BANK       VARCHAR2(60),
  ACCNTNO    VARCHAR2(40),
  TAXNO      VARCHAR2(40),
  LASTFKDATE DATE,
  LASTNO     VARCHAR2(20),
  FKDATE     DATE,
  JHJE       NUMBER(14,2),
  JHTZ       NUMBER(14,2),
  XSJE       NUMBER(14,2) default 0,
  XSTZ       NUMBER(14,2),
  FYJE       NUMBER(14,2) default 0,
  ZKJE       NUMBER(14,2) default 0,
  CYTZ       NUMBER(14,2) default 0,
  FPJE       NUMBER(14,2) default 0,
  YFJE       NUMBER(14,2) default 0,
  SFJE       NUMBER(14,2) default 0,
  FKFS       CHAR(2),
  N1         NUMBER,
  N2         NUMBER,
  N3         NUMBER,
  N4         NUMBER,
  N5         NUMBER,
  C1         VARCHAR2(80),
  C2         VARCHAR2(80),
  C3         VARCHAR2(80),
  C4         VARCHAR2(80),
  C5         VARCHAR2(80),
  C6         VARCHAR2(80),
  C7         VARCHAR2(80),
  C8         VARCHAR2(80),
  C9         VARCHAR2(80),
  C10        VARCHAR2(80),
  D1         DATE,
  D2         DATE,
  SUPSIGN    VARCHAR2(30),
  INPUTOR    VARCHAR2(15),
  INPUTDATE  DATE,
  AUDITOR    VARCHAR2(15),
  AUDITDATE  DATE,
  BUYER      VARCHAR2(15),
  PERSON1    VARCHAR2(15),
  PERSON2    VARCHAR2(15),
  PERSON3    VARCHAR2(15),
  PERSON4    VARCHAR2(15),
  PERSON5    VARCHAR2(15),
  PRTCOUNT   NUMBER(4) default 0,
  MEMO       VARCHAR2(400),
  D3         DATE,
  D4         DATE,
  PERSON6    VARCHAR2(15),
  WMID       CHAR(1),
  D5         DATE,
  STOPOPER1  VARCHAR2(15),
  STOPOPER2  VARCHAR2(4),
  PERSON7    VARCHAR2(15),
  D6         DATE,
  SHEETTYPE  CHAR(1) not null
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPPAY_IH
  is '[SPH]????????????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.BILLNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.HANDNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.BTYPE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.FLAG
  is '???(N???,W???,G???)';
comment on column DBUSRVSS.FIN_SUPPAY_IH.BSOURCE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PCID
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.MRID
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.SUPID
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.CONTNO
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.BANK
  is '???????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.ACCNTNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.TAXNO
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.LASTFKDATE
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.LASTNO
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.FKDATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.JHJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.JHTZ
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.XSJE
  is '???????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.XSTZ
  is '???????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.FYJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.ZKJE
  is '????????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.CYTZ
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.FPJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.YFJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.SFJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.FKFS
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.C1
  is '??????????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.C2
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.C3
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.C4
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.C5
  is '????????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.C6
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.C9
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.D1
  is '???1???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.D2
  is '???2???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.SUPSIGN
  is '????????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.INPUTOR
  is 'INPUTOR';
comment on column DBUSRVSS.FIN_SUPPAY_IH.INPUTDATE
  is 'INPUTDATE';
comment on column DBUSRVSS.FIN_SUPPAY_IH.AUDITOR
  is 'AUDITOR';
comment on column DBUSRVSS.FIN_SUPPAY_IH.AUDITDATE
  is 'AUDITDATE';
comment on column DBUSRVSS.FIN_SUPPAY_IH.BUYER
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON1
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON2
  is '????????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON3
  is '?????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON4
  is '?????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON5
  is '?????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PRTCOUNT
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.MEMO
  is '???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.D3
  is '???3???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.D4
  is '???0???';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON6
  is '?????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.WMID
  is '??????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.D5
  is '?????????';
comment on column DBUSRVSS.FIN_SUPPAY_IH.STOPOPER1
  is '?????  --add by lijl';
comment on column DBUSRVSS.FIN_SUPPAY_IH.STOPOPER2
  is '??????   --add by lijl';
comment on column DBUSRVSS.FIN_SUPPAY_IH.PERSON7
  is '?????;
comment on column DBUSRVSS.FIN_SUPPAY_IH.D6
  is '??????';
alter table DBUSRVSS.FIN_SUPPAY_IH
  add constraint PK_FIN_SUPPAY_IH primary key (BILLNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPPAY_IH_AUDITDATE on DBUSRVSS.FIN_SUPPAY_IH (AUDITDATE)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPPAY_IH_FLAG on DBUSRVSS.FIN_SUPPAY_IH (FLAG)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPPAY_IH_SUPID on DBUSRVSS.FIN_SUPPAY_IH (SUPID, PCID)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPSETTLE_ID
prompt ===============================
prompt
create table DBUSRVSS.FIN_SUPSETTLE_ID
(
  BILLNO    VARCHAR2(20) not null,
  ROWNO     NUMBER(6) not null,
  MRID      VARCHAR2(20) not null,
  MAID      VARCHAR2(20),
  WMID      CHAR(1),
  CONTNO    VARCHAR2(20),
  YBILLNO   VARCHAR2(20),
  YDJBH     VARCHAR2(20),
  LASTDATE  DATE,
  THISDATE  DATE,
  C1        VARCHAR2(20),
  C2        VARCHAR2(20),
  C3        VARCHAR2(40),
  C4        VARCHAR2(40),
  C5        VARCHAR2(60),
  D1        DATE,
  D2        DATE,
  N1        NUMBER default 0 not null,
  N2        NUMBER default 0 not null,
  N3        NUMBER default 0 not null,
  N4        NUMBER default 0 not null,
  N5        NUMBER default 0 not null,
  N6        NUMBER default 0 not null,
  N7        NUMBER default 0 not null,
  N8        NUMBER default 0 not null,
  N9        NUMBER default 0 not null,
  N10       NUMBER default 0 not null,
  N11       NUMBER default 0 not null,
  N12       NUMBER default 0 not null,
  N13       NUMBER default 0 not null,
  N14       NUMBER default 0 not null,
  N15       NUMBER default 0 not null,
  N16       NUMBER default 0 not null,
  N17       NUMBER default 0 not null,
  N18       NUMBER default 0 not null,
  N19       NUMBER default 0 not null,
  N20       NUMBER default 0 not null,
  N21       NUMBER default 0 not null,
  N22       NUMBER default 0 not null,
  N23       NUMBER default 0 not null,
  N24       NUMBER default 0 not null,
  N25       NUMBER default 0 not null,
  N26       NUMBER default 0 not null,
  N27       NUMBER default 0 not null,
  N28       NUMBER default 0 not null,
  N29       NUMBER default 0 not null,
  N30       NUMBER default 0 not null,
  SHEETTYPE CHAR(1) not null,
  N31       NUMBER default 0 not null,
  N32       NUMBER default 0 not null
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPSETTLE_ID
  is '[SSD]????????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.BILLNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.ROWNO
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.MRID
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.MAID
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.WMID
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.CONTNO
  is 'SSDCONTNO';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.YBILLNO
  is '???????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.YDJBH
  is 'SSDYDJBH';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.LASTDATE
  is 'SSDLASTDATE';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.THISDATE
  is 'SSDTHISDATE';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.C1
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.C2
  is 'SSDVC2';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.C3
  is 'SSDVC3';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.C4
  is 'SSDVC4';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.C5
  is 'SSDVC5';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.D1
  is 'SSDD1';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.D2
  is 'SSDD2';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N1
  is 'SSDN1';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N2
  is 'SSDN2';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N3
  is 'SSDN3';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N4
  is 'SSDN4';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N5
  is 'SSDN5';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N6
  is 'SSDN6';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N7
  is 'SSDN7';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N8
  is 'SSDN8';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N9
  is 'SSDN9';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N10
  is 'SSDN10';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N11
  is 'SSDN11';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N12
  is 'SSDN12';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N13
  is 'SSDN13';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N14
  is 'SSDN14';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N15
  is 'SSDN15';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N16
  is 'SSDN16';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N17
  is 'SSDN17';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N18
  is 'SSDN18';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N19
  is 'SSDN19';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N20
  is 'SSDN20';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N21
  is 'SSDN21';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N22
  is 'SSDN22';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N23
  is 'SSDN23';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N24
  is 'SSDN24';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N25
  is 'SSDN25';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N26
  is 'SSDN26';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N27
  is 'SSDN27';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N28
  is 'SSDN28';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N29
  is 'SSDN29';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N30
  is 'SSDN30';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N31
  is 'SSDN31';
comment on column DBUSRVSS.FIN_SUPSETTLE_ID.N32
  is 'SSDN32';
alter table DBUSRVSS.FIN_SUPSETTLE_ID
  add constraint PK_FIN_SUPSETTLE_ID primary key (BILLNO, ROWNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_ID_THISDATE on DBUSRVSS.FIN_SUPSETTLE_ID (THISDATE, MRID)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_ID_YBILLNO on DBUSRVSS.FIN_SUPSETTLE_ID (YBILLNO)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPSETTLE_IE
prompt ===============================
prompt
create table DBUSRVSS.FIN_SUPSETTLE_IE
(
  BILLNO     VARCHAR2(20) not null,
  ROWNO      NUMBER(6) not null,
  MRID       VARCHAR2(20),
  MAID       VARCHAR2(20),
  CHARGEID   CHAR(2) not null,
  CHARGENAME VARCHAR2(30) not null,
  CHARGETYPE CHAR(1) not null,
  MONEY      NUMBER(14,2) not null,
  CALCRATE   NUMBER,
  CALCBASE   NUMBER(14,2),
  CONTNO     VARCHAR2(20),
  CALMRATE   NUMBER,
  PPID       VARCHAR2(10),
  GDID       VARCHAR2(13),
  RPTIME     CHAR(1),
  RTYPE      CHAR(1),
  SHEETTYPE  CHAR(1) not null
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPSETTLE_IE
  is 'FIN_SUPSETTLE_IE';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.BILLNO
  is '?????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.ROWNO
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.MRID
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.MAID
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CHARGEID
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CHARGENAME
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CHARGETYPE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.MONEY
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CALCRATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CALCBASE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CONTNO
  is '?????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.CALMRATE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.PPID
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.GDID
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.RPTIME
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IE.RTYPE
  is '??????';
alter table DBUSRVSS.FIN_SUPSETTLE_IE
  add constraint PK_FIN_SUPSETTLE_IE primary key (BILLNO, ROWNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_IE_CX on DBUSRVSS.FIN_SUPSETTLE_IE (CONTNO)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt
prompt Creating table FIN_SUPSETTLE_IH
prompt ===============================
prompt
create table DBUSRVSS.FIN_SUPSETTLE_IH
(
  BILLNO    VARCHAR2(20) not null,
  HANDNO    VARCHAR2(20) not null,
  BTYPE     CHAR(3) not null,
  FLAG      CHAR(1) not null,
  BSOURCE   CHAR(1) not null,
  PCID      VARCHAR2(20) not null,
  SUPID     VARCHAR2(8) not null,
  WMID      CHAR(1) not null,
  MRID      VARCHAR2(20),
  MAID      VARCHAR2(20),
  CONTNO    VARCHAR2(15),
  BANK      VARCHAR2(60),
  ACCNTNO   VARCHAR2(40),
  TAXNO     VARCHAR2(40),
  LASTDATE  DATE,
  THISDATE  DATE not null,
  LASTYE    NUMBER(14,2) not null,
  THISYE    NUMBER(14,2) not null,
  OGDJE     NUMBER(14,2) not null,
  MGDJE     NUMBER(14,2) not null,
  AQDJE     NUMBER(14,2) not null,
  TOTDJE    NUMBER(14,2) not null,
  TOTYFJE   NUMBER(14,2) not null,
  TOTKK     NUMBER(14,2) not null,
  YFKJE     NUMBER(14,2) not null,
  ADJUSTJE  NUMBER(14,2) not null,
  SJFKJE    NUMBER(14,2) not null,
  N1        NUMBER,
  N2        NUMBER,
  N3        NUMBER,
  N4        NUMBER,
  N5        NUMBER,
  N6        NUMBER,
  N7        NUMBER,
  N8        NUMBER,
  N9        NUMBER,
  N10       NUMBER,
  N11       NUMBER,
  N12       NUMBER,
  N13       NUMBER,
  N14       NUMBER,
  N15       NUMBER,
  N16       NUMBER,
  N17       NUMBER,
  N18       NUMBER,
  N19       NUMBER,
  N20       NUMBER,
  N21       NUMBER,
  N22       NUMBER,
  N23       NUMBER,
  N24       NUMBER,
  N25       NUMBER,
  C1        VARCHAR2(20),
  C2        VARCHAR2(80),
  C3        VARCHAR2(60),
  C4        VARCHAR2(40),
  C5        VARCHAR2(80),
  C6        VARCHAR2(80),
  C7        VARCHAR2(80),
  C8        VARCHAR2(80),
  C9        VARCHAR2(80),
  C10       VARCHAR2(80),
  D1        DATE,
  D2        DATE,
  SUPSIGN   VARCHAR2(30),
  INPUTOR   VARCHAR2(15) not null,
  INPUTDATE DATE not null,
  AUDITOR   VARCHAR2(15),
  AUDITDATE DATE,
  BUYER     VARCHAR2(15),
  PERSON1   VARCHAR2(15),
  PERSON2   VARCHAR2(15),
  PERSON3   VARCHAR2(15),
  PERSON4   VARCHAR2(15),
  PERSON5   VARCHAR2(15),
  PRTCOUNT  NUMBER(4) default 0 not null,
  MEMO      VARCHAR2(400),
  N26       NUMBER,
  N27       NUMBER,
  N28       NUMBER,
  N29       NUMBER,
  N30       NUMBER,
  SHEETTYPE CHAR(1) not null,
  EDATE     DATE
)
tablespace DATA_SPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
comment on table DBUSRVSS.FIN_SUPSETTLE_IH
  is '[SSH]?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.BILLNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.HANDNO
  is 'SSHDJBH';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.BTYPE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.FLAG
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.BSOURCE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PCID
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.SUPID
  is '?????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.WMID
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.MRID
  is 'SSHMKT';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.MAID
  is 'SSHMFID';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.CONTNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.BANK
  is '???????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.ACCNTNO
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.TAXNO
  is '?????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.LASTDATE
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.THISDATE
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.LASTYE
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.THISYE
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.OGDJE
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.MGDJE
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.AQDJE
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.TOTDJE
  is '???????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.TOTYFJE
  is '?????????,??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.TOTKK
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.YFKJE
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.ADJUSTJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.SJFKJE
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N1
  is '???????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N2
  is 'SSHN2???????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N3
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N4
  is '???????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N5
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N6
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N7
  is 'SSHN7?????????????????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N8
  is '???????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N9
  is 'SSHN9?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N10
  is 'SSHN10';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N11
  is 'SSHN11';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N12
  is 'SSHN12';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N13
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N14
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N15
  is 'SSHN15';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N16
  is 'SSHN16';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N17
  is 'SSHN17';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N18
  is '0.13???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N19
  is '0.17???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N20
  is '0.04???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N21
  is 'SSHN21';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N22
  is 'SSHN22';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N23
  is '??????????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N24
  is '?????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N25
  is 'SSHN25';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C1
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C2
  is 'SSHVC2';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C3
  is '????????0??????,2????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C4
  is '??????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C5
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C6
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C7
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C8
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C9
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.C10
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.D1
  is 'SSHD1';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.D2
  is 'SSHD2';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.SUPSIGN
  is '????????;
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.INPUTOR
  is 'INPUTOR';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.INPUTDATE
  is 'INPUTDATE';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.AUDITOR
  is 'AUDITOR';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.AUDITDATE
  is 'AUDITDATE';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.BUYER
  is 'BUYER';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PERSON1
  is 'PERSON1';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PERSON2
  is 'PERSON2';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PERSON3
  is 'PERSON3';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PERSON4
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PERSON5
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.PRTCOUNT
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.MEMO
  is '???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N26
  is '0.06???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N27
  is '0.1???';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N29
  is '?????????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.N30
  is '??????';
comment on column DBUSRVSS.FIN_SUPSETTLE_IH.EDATE
  is '?????????110119??;
alter table DBUSRVSS.FIN_SUPSETTLE_IH
  add constraint PK_FIN_SUPSETTLE_IH primary key (BILLNO, SHEETTYPE)
  using index 
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_IH_AUDITDATE on DBUSRVSS.FIN_SUPSETTLE_IH (AUDITDATE)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_IH_BTYPE on DBUSRVSS.FIN_SUPSETTLE_IH (BTYPE, FLAG)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_IH_HANDNO on DBUSRVSS.FIN_SUPSETTLE_IH (HANDNO)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_IH_PM on DBUSRVSS.FIN_SUPSETTLE_IH (PCID, MRID)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create index DBUSRVSS.IDX_FIN_SUPSETTLE_IH_SUPID on DBUSRVSS.FIN_SUPSETTLE_IH (SUPID, WMID, PCID, BTYPE, CONTNO, N11, BSOURCE)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
create unique index DBUSRVSS.IDX_FIN_SUPSETTLE_IH_THISDATE on DBUSRVSS.FIN_SUPSETTLE_IH (THISDATE, SUPID, WMID, PCID, PERSON4, PERSON5, BSOURCE, CONTNO)
  tablespace INDX_SPC
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
 
  