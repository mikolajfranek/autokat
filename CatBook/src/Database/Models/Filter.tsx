import Realm, { BSON, ObjectSchema, Types } from "realm";

export default class Filter extends Realm.Object<Filter> {
    _id!: BSON.ObjectId;
    _created_at: Types.Date = new Date();
    _name!: Types.String;

    static schema: ObjectSchema = {
        name: 'Filter',
        properties: {
            _id: 'objectId',
            _created_at: { type: 'date', default: () => new Date() },
            _name: { type: 'string', indexed: 'full-text' }
        },
        primaryKey: '_id'
    };
}