import React, { Component } from 'react';
import { findDOMNode } from 'react-dom';
import { observable, computed, toJS } from 'mobx';
import { observer, inject } from 'mobx-react';
import nj from 'nornj';
import { registerTmpl } from 'nornj-react';
import { autobind } from 'core-decorators';
import 'flarej/lib/components/antd/table';
import 'flarej/lib/components/antd/input';
import 'flarej/lib/components/antd/button';
import 'flarej/lib/components/antd/pagination';
import 'flarej/lib/components/antd/tabs';
import 'flarej/lib/components/antd/tree';
import 'flarej/lib/components/antd/checkbox';
import Modal from 'flarej/lib/components/antd/modal';
import Tree from 'flarej/lib/components/antd/tree';
import Input from 'flarej/lib/components/antd/input';
import Message from 'flarej/lib/components/antd/message';
import Notification from '../../../utils/notification';
import styles from './search.m.scss';
import tmpls from './search.t.html';

//页面容器组件
@registerTmpl('Search')
@inject('store')
@observer
export default class Search extends Component {
  @observable detailModalVisible = false;
  @observable inputRole = '';
  @observable detailData = [];
  @observable selectedRowKeys = [];
  @observable selectedRows = [];

  componentDidMount() {
    const { store: { search } } = this.props;

    const closeLoading = Message.loading('正在获取数据...', 0);
    Promise.all([
      search.getRoleManagementData(),
      search.getRoleMenuTree().then(() => search.initTree())
    ]).then(() => {
      closeLoading();
    });
  }

  @autobind
  onInputRole(e) {
    this.inputRole = e.target.value;
  }

  @autobind
  onSearch() {
    if (this.inputRole == '') {
      const closeLoading = Message.loading('正在获取数据...', 0);
      Promise.all([
        this.props.store.search.getRoleManagementData(),
      ]).then(() => {
        closeLoading();
      });
    } else {
      const { store: { search } } = this.props;
      const searchRole = search.tableDataO.filter(n => n.name.indexOf(this.inputRole.trim()) > -1);
      search.setTableData(searchRole);
    }
  }

  @autobind
  onAddRole() {
    const { store: { search } } = this.props;
    search.setAddModalVisible(true);
    search.setDisable(true);
    search.setActiveKey('tab1');
    search.setAddInputRole('');
    search.setAddInputDes('');
  }

  @autobind
  onDeleteRole() {
    const { store: { search } } = this.props;
    if (this.selectedRowKeys.length == 0) {
      Notification.error({ description: '请勾选要删除的角色！', duration: 3 });
    } else {
      Modal.confirm({
        title: '你确认要删除角色吗？',
        onOk: () => {
          const closeLoading = Message.loading('正在获取数据...', 0);
          const roleId = this.selectedRows.map((item) => item.roleId);

          Promise.all([
            search.deleteRole({ roleId: roleId })
          ]).then(() => {
            search.getRoleManagementData();
          }).then(() => {
            this.selectedRowKeys = [];
            closeLoading();
          });
        }
      });
    }
  }

  @computed get tableColumns() {
    return [{
      title: '序号',
      dataIndex: 'key',
    }, {
      title: '角色名称',
      dataIndex: 'name',
    }, {
      title: '角色描述',
      dataIndex: 'describe',
    }, {
      title: '创建时间',
      dataIndex: 'cTime',
    }, {
      title: '修改时间',
      dataIndex: 'mTime',
    }, {
      title: '操作',
      dataIndex: 'handler',
      render: (text, record, index) => nj `
        <span>
          <a href="javascript:;" onClick=${()=>this.onEdit(record, index)} className="btn-link">编辑</a>
          <a href="javascript:;" onClick=${()=>this.onDetail(record, index)} className="btn-link">用户明细</a>
        </span>
      ` (),
    }];
  }

  @autobind
  onEdit(record, index) {
    const { store: { search } } = this.props;
    search.setEditModalVisible(true);
    search.setSaveBtnDisabled(true);
    search.setActiveKey('tab1');
    search.setAddInputRole(record.name);
    search.setAddInputDes(record.describe);
    search.setRoleId(record.roleId);
    search.setDisable(false);

    const closeLoading = Message.loading('正在获取数据...', 0);
    Promise.all([
      search.getRoleMenuTree({ roleId: record.roleId }).then(() => search.initTree()),
    ]).then(() => {
      closeLoading();
    });
  }

  @autobind
  onDetail(record, index) {
    const { store: { search } } = this.props;
    search.setDetailModalVisible(true);
    search.setDetailData(record.users);
  }

  getRowSelection() {
    return {
      selectedRowKeys: this.selectedRowKeys,
      onChange: (selectedRowKeys, selectedRows) => {
        this.selectedRowKeys = selectedRowKeys;
        this.selectedRows = selectedRows;
      }
    };
  }

  render() {
    const { store: { search } } = this.props;
    return tmpls.container(this.props, this, {
      styles,
      search,
      tableData: toJS(search.tableData),
      rowSelection: this.getRowSelection(),
    });
  }
}

@registerTmpl('ModalFormSearch')
@inject('store')
@observer
class ModalForm extends Component {
  @observable autoExpandParent = false;

  //获取树节点的展开形式
  getExpandedKeys(arr) {
    return arr.filter(n => n.level == 1 || n.level == 2).map(m => { return m.id.toString(); });
  }

  getDefaultCheckedKeys() {
    let keys = [];
    this.props.store.search.menuData.filter(n => n.level == 3)
      .forEach(item => {
        if (item.selected) {
          keys.push(item.id.toString());
        }
      });
    return keys;
  }

  //获取选中的 checkbox 包含父级未选中状态
  getAllCheckedKeys(key) {
    const _map = toJS(this.props.store.search.authTreeDataMap);
    if (key.length > 1) {
      let pids = key.map(item => { return _map[item].pids; });
      return Array.from(new Set([].concat(...pids)));
    } else {
      return _map[key].pids;
    }
  }

  @autobind
  onExpand(expandedKeys) {
    const { store: { search } } = this.props;
    search.setExpandedKeys(expandedKeys);
    this.autoExpandParent = true;
  }

  @autobind
  onCheck(checkedKeys) {
    const { store: { search } } = this.props;
    search.setSaveBtnDisabled(false);
    search.setCheckedKeys(checkedKeys);

    if (checkedKeys.length == 0) {
      search.setMenuIds([]);
    } else {
      let allChecked = Array.from(new Set(this.getAllCheckedKeys(checkedKeys).concat(checkedKeys)));
      search.setMenuIds(allChecked);
    }
  }

  @autobind
  onAddModalCancel() {
    if (this.props.tabName == '增加角色') {
      this.props.store.search.setAddModalVisible(false);
    }
    else {
      this.props.store.search.setEditModalVisible(false);
    }
    this.props.store.search.setSaveBtnDisabled(false);
  }

  @autobind
  onTabChange(key) {
    this.props.store.search.setActiveKey(key);
  }

  @autobind
  onAddInputRoleChange(e) {
    this.props.store.search.setAddInputRole(e.target.value);
  }

  @autobind
  onAddInputDesChange(e) {
    this.props.store.search.setAddInputDes(e.target.value);
  }

  @autobind
  onAddSaveRole() {
    const { store: { search } } = this.props;

    if (search.addInputRole.trim() == '') {
      Notification.error({ description: '请输入角色名称！', duration: 1 });
    } else {
      const closeLoading = Message.loading('正在获取数据...', 0);
      let params = {
        roleName: search.addInputRole,
        roleDescribe: search.addInputDes
      };
      if (this.props.tabName == '编辑角色' && search.roleId != null) {
        params.roleId = search.roleId;
      }

      Promise.all([
        search.saveRole(params)
      ]).then(() => {
        search.getRoleManagementData();
      }).then(() => {
        search.setActiveKey('tab2');
        closeLoading();
      });
    }
  }

  @autobind
  onAddCancel() {
    const { store: { search } } = this.props;
    if (this.props.tabName == '增加角色') {
      search.setAddModalVisible(false);
    } else {
      search.setEditModalVisible(false);
    }
  }

  @autobind
  onSavePermission() {
    const { store: { search } } = this.props;

    if (this.props.tabName == '增加角色') {
      console.log(search.menuIds);
      search.saveRolePermission({
        roleId: search.addRoleId,
        menuIds: search.menuIds
      }).then(() => search.setAddModalVisible(false));
    } else {
      search.saveRolePermission({
        roleId: search.roleId,
        menuIds: search.menuIds
      }).then(() => search.setEditModalVisible(false));
    }
  }

  disabledTreeNodes = ['权限管理', '角色管理'];

  render() {
    const { store: { search } } = this.props;
    const TreeNode = Tree.TreeNode;
    let level = 1;
    const loop = data => data.map((item) => {
      const disableCheckbox = this.disabledTreeNodes.indexOf(item.title) > -1 ? true : false;

      if (item.children) {
        const disabled = level == 1 ? true : item.children.filter(n => this.disabledTreeNodes.indexOf(n.title) > -1).length > 0;
        level++;

        return nj `
          <${TreeNode} key=${item.key} title=${item.title} disableCheckbox=${disableCheckbox} disabled=${disabled}>
            ${loop(item.children)}
          </${TreeNode}>
        ` ();
      }
      return nj `<${TreeNode} key=${item.key} title=${item.title} disableCheckbox=${disableCheckbox} />` ();
    });

    return tmpls.modalForm({
      components: { 'ant-TextArea': Input.TextArea },
      styles,
      search,
      loop,
      treeData: toJS(search.authTreeData) || [],
    }, this.props, this);
  }
}

@registerTmpl('ModalDetailSearch')
@inject('store')
@observer
class ModalDetail extends Component {
  @autobind
  onDetailModalCancel() {
    const { store: { search } } = this.props;
    search.setDetailModalVisible(false);
  }

  @computed get detailColumns() {
    return [{
      title: '序号',
      dataIndex: 'key',
    }, {
      title: '登录名',
      dataIndex: 'loginName',
    }, {
      title: '姓名',
      dataIndex: 'name',
    }, {
      title: '邮箱',
      dataIndex: 'email',
    }, {
      title: '部门',
      dataIndex: 'department',
    }, {
      title: '职务',
      dataIndex: 'duty',
    }, {
      title: '开通时间',
      dataIndex: 'oTime',
    }];
  }

  render() {
    const { store: { search } } = this.props;
    return tmpls.modalDetail(this.props, this, {
      styles,
      search,
      getDetailRowKey: (record, index) => `${record.key}-${index}`
    });
  }
}