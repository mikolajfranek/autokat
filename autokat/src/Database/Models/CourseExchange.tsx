import Realm, { BSON, Types, ObjectSchema } from "realm";
import React from "react";
import { View } from "react-native";
import { Text } from "react-native-paper";

export default class CourseExchange extends Realm.Object<CourseExchange> {
    _id!: BSON.ObjectId;
    
    _created_at: Types.Date = new Date();
    _eur_effectived_at!: Types.Date;
    _eur_mid!: Types.Double;
    _usd_effectived_at!: Types.Date;
    _usd_mid!: Types.Double;

    static schema: ObjectSchema = {
        name: 'CourseExchange',
        properties: {
            _id: 'objectId',

            _created_at: { type: 'date', default: () => new Date() },
            _eur_effectived_at: 'date',
            _eur_mid: 'double',
            _usd_effectived_at: 'date',
            _usd_mid: 'double',
        },
        primaryKey: '_id'
    };

    getJSX(): React.JSX.Element {
        return (
            <View style={{ flexDirection: 'row', alignItems: 'center', marginBottom: 10 }}>
                <Text>{this._eur_mid.toString()}</Text>
                <Text style={{ marginLeft: 10 }}>{this._usd_mid.toString()}</Text>
                <Text style={{ marginLeft: 10 }}>{this._created_at.toString()}</Text>
            </View>
        );
    }
}