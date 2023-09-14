-- auto-generated definition
create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    user_name     varchar(256)                       null comment '用户昵称',
    user_account  varchar(256)                       null comment '用户账户',
    avatarUrl     varchar(1024)                      null comment '用户头像',
    user_password varchar(512)                       not null comment '用户密码',
    gender        tinyint                            null comment '性别  0-女 1-男 2-保密',
    phone         varchar(128)                       null comment '手机号',
    email         varchar(256)                       null comment '邮箱',
    user_status   int      default 0                 not null comment '用户状态(正常、封禁等，用0，1等数字表示)',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除(逻辑删除。为了保护数据，设置0，1表示该条数据是否删除)',
    user_role     int      default 0                 not null comment '用户权限 0 -普通用户 1 -管理员',
    allow_code    varchar(512)                       null comment '特殊注册码，注册时输入注册码才能注册成为网站用户'
)
    comment '用户';