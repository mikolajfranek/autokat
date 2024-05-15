import Realm, { ObjectSchema, Types } from "realm";

export default class CourseMetal extends Realm.Object<CourseMetal> {
    _id!: Types.Int;
    platinum_bid!: Types.Decimal128;
    palladium_bid!: Types.Decimal128;
    rhodium_bid!: Types.Decimal128;
    effectived_at!: Types.Date;

    static schema: ObjectSchema = {
        name: 'CourseMetal',
        properties: {
            _id: 'int',
            platinum_bid: 'decimal128',
            palladium_bid: 'decimal128',
            rhodium_bid: 'decimal128',
            effectived_at: 'date'
        },
        primaryKey: '_id'
    };
}