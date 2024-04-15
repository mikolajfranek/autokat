import { Model } from '@nozbe/watermelondb';
import { date, readonly, text } from '@nozbe/watermelondb/decorators';

export default class CourseExchange extends Model {
    static table = 'courses_exchange';

    @readonly @date('created_at') createdAt;
    @text('eur_pln') eurPln;
    @text('usd_pln') usdPln;
}