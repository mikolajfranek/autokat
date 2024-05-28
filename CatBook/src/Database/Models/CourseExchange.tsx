import React from "react";
import { View } from "react-native";
import { Text } from "react-native-paper";
import Realm, { BSON, ObjectSchema, Types } from "realm";

export default class CourseExchange extends Realm.Object<CourseExchange> {
    _id!: BSON.ObjectId;
    _created_at: Types.Date = new Date();
    _value_eur_mid!: Types.Double;
    _value_usd_mid!: Types.Double;
    _effectived_at!: Types.Date;

    static schema: ObjectSchema = {
        name: 'CourseExchange',
        properties: {
            _id: 'objectId',
            _created_at: { type: 'date', default: () => new Date() },
            _value_eur_mid: 'double',
            _value_usd_mid: 'double',
            _effectived_at: 'date'
        },
        primaryKey: '_id'
    };

    getJSX(): React.JSX.Element {
        return (
            <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 10 }}>
                <Text>{this._value_eur_mid.toString()}</Text>
                <Text style={{ marginLeft: 10 }}>{this._value_usd_mid.toString()}</Text>
                <Text style={{ marginLeft: 10 }}>{this._effectived_at.toString()}</Text>
            </View>
        );
    }
}