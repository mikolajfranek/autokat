import Realm, { ObjectSchema, Types } from "realm";

export default class Catalyst extends Realm.Object<Catalyst> {
    _id!: Types.Int;
    type!: Types.Int;
    name!: Types.String;
    brand!: Types.String;
    picture_id!: Types.String;
    thumbnail?: Types.Data;
    weight!: Types.Decimal128;
    platinum!: Types.Decimal128;
    palladium!: Types.Decimal128;
    rhodium!: Types.Decimal128;

    static schema: ObjectSchema = {
        name: 'Catalyst',
        properties: {
            _id: 'int',
            type: { type: 'int', indexed: true },
            name: { type: 'string', indexed: 'full-text' },
            brand: { type: 'string', indexed: 'full-text' },
            picture_id: 'string',
            thumbnail: 'data?',
            weight: 'decimal128',
            platinum: 'decimal128',
            palladium: 'decimal128',
            rhodium: 'decimal128'
        },
        primaryKey: '_id'
    };
}