// Plain-English names for the backend's enum values.
export const AGGREGATION_LABELS = { SUM: 'Sum', COUNT: 'Count', AVG: 'Average', MIN: 'Minimum', MAX: 'Maximum' };
export const METRIC_LABELS = { AMOUNT: 'amount', QUANTITY: 'quantity' };
export const GROUP_LABELS = { REGION: 'region', PRODUCT: 'product', SALE_DATE: 'sale date' };
export const CHART_LABELS = { BAR: 'Bar', LINE: 'Line', PIE: 'Pie', AREA: 'Area', KPI: 'KPI' };

/** "Sum of amount, by region" */
export function describeWidget(widget) {
  const base = `${AGGREGATION_LABELS[widget.aggregationType] ?? widget.aggregationType} of ${
    METRIC_LABELS[widget.metricField] ?? widget.metricField
  }`;
  return widget.groupByField ? `${base}, by ${GROUP_LABELS[widget.groupByField] ?? widget.groupByField}` : base;
}