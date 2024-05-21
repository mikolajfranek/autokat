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
}