export function filterByQuery(data, fields, query) {
  return data.filter((item) =>
    fields.some((field) =>
      item[field]?.toString().toLowerCase().includes(query.toLowerCase()),
    ),
  );
}
