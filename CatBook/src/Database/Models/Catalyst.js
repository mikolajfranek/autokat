import { Model } from '@nozbe/watermelondb';
import { date, readonly, field } from '@nozbe/watermelondb/decorators';

export default class Course extends Model {
    static table = 'catalysts';

    @readonly @date('created_at') createdAt;
    @field('picture_id') pictureId;
    @field('thumbnail') thumbnail;
    @field('is_thumbnail') isThumbnail;
    @field('name') name;
    @field('brand') brand;
    @field('platinum') platinum;
    @field('palladium') palladium;
    @field('rhodium') rhodium;
    @field('type') type;
    @field('weight') weight;
}