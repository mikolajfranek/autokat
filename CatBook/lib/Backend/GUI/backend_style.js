Object.defineProperty(exports,"__esModule",{value:true});exports.styles=exports.colorText=exports.colorSecondary=exports.colorPrimary=void 0;var _reactNative=require("react-native");var _Dimensions$get=_reactNative.Dimensions.get("window"),width=_Dimensions$get.width;var scaleFactor={_1of25:1.0/25,_1of20:1.0/20,_1of15:1.0/15,_1of10:1.0/10};var styles=exports.styles=_reactNative.StyleSheet.create({baseFont:{fontSize:width*scaleFactor._1of25,color:colorText},baseFontHeader:{fontSize:width*scaleFactor._1of15,color:colorText}});var colorPrimary=exports.colorPrimary="#363636";var colorSecondary=exports.colorSecondary="#1e3a8a";var colorText=exports.colorText="#363636";