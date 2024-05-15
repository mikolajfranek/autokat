import Realm, { BSON, ObjectSchema, Types } from "realm";

export default class CourseMetal extends Realm.Object<CourseMetal> {
    _id!: BSON.ObjectId;
    _created_at: Types.Date = new Date();
    _platinum_bid!: Types.Double;
    _palladium_bid!: Types.Double;
    _rhodium_bid!: Types.Double;
    _effectived_at!: Types.Date;

    static schema: ObjectSchema = {
        name: 'CourseMetal',
        properties: {
            _id: 'objectId',
            _created_at: { type: 'date', default: () => new Date() },
            _platinum_bid: 'double',
            _palladium_bid: 'double',
            _rhodium_bid: 'double',
            _effectived_at: 'date'
        },
        primaryKey: '_id'
    };
}