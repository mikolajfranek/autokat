import { Model } from '@nozbe/watermelondb';
import { date, readonly, text } from '@nozbe/watermelondb/decorators';

export default class CourseExchange extends Model {
    static table = 'courses_exchange';

    @readonly @date('created_at') createdAt;
    @number('type') type;
    @text('mid') mid;
    @date('effectived_at') effectivedAt;

    //TODO
    @writer static async add(eurPln, usdPln) {
        const newItem = await this.collections.get('courses_exchange').create(exchange => {
            exchange.eurPln.set(eurPln);
            exchange.usdPln.set(usdPln);
        });
        return newItem;
    }
}