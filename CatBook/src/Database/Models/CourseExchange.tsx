import Realm, { BSON, ObjectSchema, Types } from "realm";

export default class CourseExchange extends Realm.Object<CourseExchange> {
    _id!: BSON.ObjectId;
    _type!: Types.Int;
    _value_mid!: Types.Decimal128;
    _effectived_at!: Types.Date;

    static schema: ObjectSchema = {
        name: 'CourseExchange',
        properties: {
            _id: 'objectId',
            _type: { type: 'int', indexed: true },
            _value_mid: 'decimal128',
            _effectived_at: 'date'
        },
        primaryKey: '_id'
    };
}