var _interopRequireDefault=require("@babel/runtime/helpers/interopRequireDefault");Object.defineProperty(exports,"__esModule",{value:true});exports.default=void 0;var _initializerDefineProperty2=_interopRequireDefault(require("@babel/runtime/helpers/initializerDefineProperty"));var _createClass2=_interopRequireDefault(require("@babel/runtime/helpers/createClass"));var _classCallCheck2=_interopRequireDefault(require("@babel/runtime/helpers/classCallCheck"));var _possibleConstructorReturn2=_interopRequireDefault(require("@babel/runtime/helpers/possibleConstructorReturn"));var _getPrototypeOf2=_interopRequireDefault(require("@babel/runtime/helpers/getPrototypeOf"));var _assertThisInitialized2=_interopRequireDefault(require("@babel/runtime/helpers/assertThisInitialized"));var _inherits2=_interopRequireDefault(require("@babel/runtime/helpers/inherits"));var _applyDecoratedDescriptor2=_interopRequireDefault(require("@babel/runtime/helpers/applyDecoratedDescriptor"));var _initializerWarningHelper2=_interopRequireDefault(require("@babel/runtime/helpers/initializerWarningHelper"));var _watermelondb=require("@nozbe/watermelondb");var _decorators=require("@nozbe/watermelondb/decorators");var _dec,_dec2,_class,_descriptor,_descriptor2,_Filter;function _callSuper(t,o,e){return o=(0,_getPrototypeOf2.default)(o),(0,_possibleConstructorReturn2.default)(t,_isNativeReflectConstruct()?Reflect.construct(o,e||[],(0,_getPrototypeOf2.default)(t).constructor):o.apply(t,e));}function _isNativeReflectConstruct(){try{var t=!Boolean.prototype.valueOf.call(Reflect.construct(Boolean,[],function(){}));}catch(t){}return(_isNativeReflectConstruct=function _isNativeReflectConstruct(){return!!t;})();}var Filter=exports.default=(_dec=(0,_decorators.date)('created_at'),_dec2=(0,_decorators.field)('value'),(_class=(_Filter=function(_Model){(0,_inherits2.default)(Filter,_Model);function Filter(){var _this;(0,_classCallCheck2.default)(this,Filter);for(var _len=arguments.length,args=new Array(_len),_key=0;_key<_len;_key++){args[_key]=arguments[_key];}_this=_callSuper(this,Filter,[].concat(args));(0,_initializerDefineProperty2.default)(_this,"createdAt",_descriptor,(0,_assertThisInitialized2.default)(_this));(0,_initializerDefineProperty2.default)(_this,"value",_descriptor2,(0,_assertThisInitialized2.default)(_this));return _this;}return(0,_createClass2.default)(Filter);}(_watermelondb.Model),_Filter.table='filters',_Filter),(_descriptor=(0,_applyDecoratedDescriptor2.default)(_class.prototype,"createdAt",[_decorators.readonly,_dec],{configurable:true,enumerable:true,writable:true,initializer:null}),_descriptor2=(0,_applyDecoratedDescriptor2.default)(_class.prototype,"value",[_dec2],{configurable:true,enumerable:true,writable:true,initializer:null})),_class));