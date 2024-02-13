import { Model } from '@nozbe/watermelondb'
import { date, readonly, field } from '@nozbe/watermelondb/decorators'

export default class Filter extends Model {
    static table = 'filters';

    @readonly @date('created_at') createdAt;
    @field('value') value;
}