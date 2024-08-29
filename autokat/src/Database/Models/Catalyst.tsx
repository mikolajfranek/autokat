import Realm, { BSON, Types, ObjectSchema } from "realm";

export default class Catalyst extends Realm.Object<Catalyst> {
    _id!: BSON.ObjectId;
    _created_at: Types.Date = new Date();
    _type!: Types.Int;
    _name!: Types.String;
    _brand!: Types.String;
    _picture_id!: Types.String;
    _thumbnail?: Types.Data;
    _weight!: Types.Double;
    _platinum!: Types.Double;
    _palladium!: Types.Double;
    _rhodium!: Types.Double;

    static schema: ObjectSchema = {
        name: 'Catalyst',
        properties: {
            _id: 'objectId',
            _created_at: { type: 'date', default: () => new Date() },
            _type: 'int',
            _name: { type: 'string', indexed: 'full-text' },
            _brand: { type: 'string', indexed: 'full-text' },
            _picture_id: 'string',
            _thumbnail: 'data?',
            _weight: 'double',
            _platinum: 'double',
            _palladium: 'double',
            _rhodium: 'double'
        },
        primaryKey: '_id'
    };
}