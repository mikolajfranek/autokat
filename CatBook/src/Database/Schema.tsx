import { appSchema, tableSchema } from '@nozbe/watermelondb'

export default appSchema({
  version: 1,
  tables: [
    tableSchema({
      name: 'filters',
      columns: [
        { name: 'created_at', type: 'number' },
        { name: 'value', type: 'string', isOptional: false, isIndexed: true },
      ],
    }),
    tableSchema({
      name: 'courses',
      columns: [
        { name: 'created_at', type: 'number' },
        { name: 'platinum', type: 'string', isOptional: false },
        { name: 'palladium', type: 'string', isOptional: false },
        { name: 'rhodium', type: 'string', isOptional: false },
        { name: 'eur_pln', type: 'string', isOptional: false },
        { name: 'usd_pln', type: 'string', isOptional: false },
      ],
    }),
    tableSchema({
      name: 'catalysts',
      columns: [
        { name: 'created_at', type: 'number' },
        { name: 'picture_id', type: 'string', isOptional: false },
        { name: 'thumbnail', type: 'string', isOptional: true },
        { name: 'is_thumbnail', type: 'boolean', isOptional: false, isIndexed: true },
        { name: 'name', type: 'string', isOptional: false, isIndexed: true },
        { name: 'brand', type: 'string', isOptional: false, isIndexed: true },
        { name: 'platinum', type: 'string', isOptional: false },
        { name: 'palladium', type: 'string', isOptional: false },
        { name: 'rhodium', type: 'string', isOptional: false },
        { name: 'type', type: 'number', isOptional: false },
        { name: 'weight', type: 'string', isOptional: false },
      ],
    }),
  ]
})